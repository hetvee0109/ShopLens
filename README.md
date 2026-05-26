# 🛒 Kirana Store — Market Basket Analysis

> **"Customers who buy rice also buy ghee."**  
> Finding hidden buying patterns in kirana store transactions using the Apriori algorithm.

---

## 📌 Project Overview

This project applies **Market Basket Analysis** to synthetic kirana store sales data. The goal is to discover association rules — item pairs that frequently appear together — to help small retailers make smarter stocking and cross-selling decisions.

**Built on Day 1 of learning Data Science.** No prior ML experience required.

---

## 🎯 Problem Statement

A kirana store owner wants to know:
- Which items are bought together most often?
- If a customer picks up rice, what else are they likely to buy?
- How can shelf arrangement or combo offers increase sales?

---

## 📊 Dataset

| Property | Value |
|---|---|
| File | `sales_data.csv` |
| Total transactions | 300 |
| Number of items | 10 |
| Source | Synthetically generated (Python) |

**Items in the dataset:**
`rice` · `ghee` · `dal` · `sugar` · `oil` · `atta` · `salt` · `tea` · `soap` · `biscuit`

**Intentional patterns planted:**
- Rice + Ghee appear together ~70% of the time
- Dal + Salt appear together ~65% of the time

---

## 🔍 Sample Output

> 📸 <img width="789" height="381" alt="Screenshot 2026-05-26 161609" src="https://github.com/user-attachments/assets/d93154b0-cc8e-49f6-b9b1-fe46df115a8b" />


Expected output format:

| antecedents | consequents | support | confidence | lift |
|---|---|---|---|---|
| (rice) | (ghee) | 0.52 | 0.70 | 1.82 |
| (ghee) | (rice) | 0.52 | 0.68 | 1.78 |
| (dal) | (salt) | 0.38 | 0.63 | 1.54 |
| (salt) | (dal) | 0.38 | 0.61 | 1.49 |

**How to read this:**
- **Support 0.52** → rice and ghee appear together in 52% of all transactions
- **Confidence 0.70** → 70% of customers who buy rice also buy ghee
- **Lift 1.82** → customers are 1.82× more likely to buy ghee alongside rice than by chance alone

---

## 🛠️ Tech Stack

| Tool | Purpose |
|---|---|
| Python 3 | Core language |
| Jupyter Notebook | Interactive development |
| pandas | Data loading and manipulation |
| mlxtend | Apriori algorithm + association rules |

---

## ⚙️ How to Run

**1. Clone or download this project**
```bash
git clone https://github.com/hetvee0109/kirana-market-basket.git
cd kirana-market-basket
```

**2. Install dependencies**
```bash
pip install jupyter pandas mlxtend
```

**3. Launch Jupyter**
```bash
jupyter notebook
```

**4. Open `analysis.ipynb` and run all cells**

---

## 📁 Project Structure

```
kirana-market-basket/
│
├── sales_data.csv          # Synthetic transaction dataset (300 rows)
├── generate_data.py        # Script to regenerate the dataset
├── analysis.ipynb          # Main Jupyter notebook with Apriori analysis
└── README.md               # This file
```

---

## 📖 Key Concepts

**Market Basket Analysis** finds products that frequently co-occur in transactions.

**Apriori Algorithm** works in two steps:
1. Find all itemsets with support above a minimum threshold
2. Generate rules from those itemsets with confidence above a minimum threshold

**Three metrics matter:**

- `support` — how often the combination appears overall
- `confidence` — given the left side, how often the right side also appears
- `lift` — anything above 1.0 is a genuine pattern (not random chance)

---

## 🚀 What's Next (Day 2+)

- [ ] Test on real kirana store data
- [ ] Visualize item pairs as a network graph
- [ ] Build a simple web UI to query rules by item
- [ ] Compare Apriori vs FP-Growth algorithm performance

---

## 👤 Author

**Hetvee Rabara**  
Day 1 of my Data Science learning journey  
_Started: 26/5/26_

---

## 📄 License

MIT License — free to use, modify, and share.
