# apriori_runner.py

import sys                    # sys lets us read command-line arguments (the CSV path Java sends)
import json                   # json lets us convert Python data to JSON text
import pandas as pd           # pandas reads the CSV file
from mlxtend.frequent_patterns import apriori, association_rules
from mlxtend.preprocessing import TransactionEncoder

# ── Step A: Read the CSV file path from command-line argument ──
# When Java calls: python apriori_runner.py C:\path\to\file.csv
# sys.argv[0] = "apriori_runner.py"  (the script name)
# sys.argv[1] = "C:\path\to\file.csv"  (the file path Java passed)

if len(sys.argv) < 2:
    print(json.dumps({"error": "No CSV file path provided"}))
    sys.exit(1)

csv_path = sys.argv[1]   # This is the file path Java will send us

# ── Step B: Read the CSV ──
try:
    df = pd.read_csv(csv_path)
    df.columns = df.columns.str.strip()   # Remove any accidental whitespace in column names
except Exception as e:
    print(json.dumps({"error": f"Could not read CSV: {str(e)}"}))
    sys.exit(1)

# ── Step C: Prepare transactions ──
# Your CSV has columns: 'transaction_id' and 'items'
# 'items' column contains comma-separated item names in a single cell
# Example row:  1 | "sugar, dal, ghee, biscuit"
try:
    basket = df['items'].apply(lambda x: [item.strip() for item in x.split(',')]).tolist()
    # basket is now a list of lists:
    # [['sugar', 'dal', 'ghee', 'biscuit'], ['dal', 'oil', 'ghee'], ...]
except Exception as e:
    print(json.dumps({"error": f"Could not process columns: {str(e)}"}))
    sys.exit(1)

# ── Step D: Encode transactions into True/False matrix ──
te = TransactionEncoder()
te_array = te.fit(basket).transform(basket)   # Convert to True/False matrix
te_df = pd.DataFrame(te_array, columns=te.columns_)

# ── Step E: Run Apriori algorithm ──
frequent_itemsets = apriori(
    te_df,
    min_support=0.01,        # Item must appear in at least 1% of transactions
    use_colnames=True        # Use actual item names instead of numbers
)

# ── Step F: Check if any frequent itemsets were found ──
if frequent_itemsets.empty:
    print(json.dumps({"error": "No frequent itemsets found. Try lowering min_support."}))
    sys.exit(1)

# ── Step G: Generate association rules ──
try:
    rules = association_rules(
        frequent_itemsets,
        metric="lift",           # We measure by "lift" (how much better than random)
        min_threshold=1.0        # Lift must be at least 1.0
    )
except Exception as e:
    print(json.dumps({"error": f"Could not generate rules: {str(e)}"}))
    sys.exit(1)

# ── Step H: Check if any rules were generated ──
if rules.empty:
    print(json.dumps({"error": "No association rules found. Try lowering min_threshold."}))
    sys.exit(1)

# ── Step I: Pick top 10 rules and convert to JSON ──
top_rules = rules.nlargest(10, 'lift')   # Get top 10 by lift score

results = []
for _, row in top_rules.iterrows():
    results.append({
        "antecedents": list(row['antecedents']),    # "If customer buys these..."
        "consequents": list(row['consequents']),    # "...they also buy these"
        "support": round(float(row['support']), 4),
        "confidence": round(float(row['confidence']), 4),
        "lift": round(float(row['lift']), 4)
    })

# ── Step J: Print JSON to console ──
# Java will READ this console output — this is how Java gets the data
print(json.dumps({"success": True, "rules": results}))