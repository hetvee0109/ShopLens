// RulesTable.jsx
// Displays association rules in a formatted HTML table
// Props:
//   rules → array of rule objects from the backend
//   title → string heading shown above the table

function RulesTable({ rules, title }) {
  // If the rules array is empty or doesn't exist, show a friendly message
  if (!rules || rules.length === 0) {
    return (
      <div className="empty-state">
        <p>No rules to display yet. Upload a CSV file to get started.</p>
      </div>
    );
  }

  return (
    <div className="table-container">
      {/* The title prop lets the parent label this table differently
          for "Upload Results" vs "History" */}
      <h2 className="table-title">{title}</h2>
      <p className="rule-count">{rules.length} rule(s) found</p>

      {/* overflow-x auto on the container allows horizontal scrolling
          on small screens so the table doesn't break the layout */}
      <div className="table-scroll">
        <table className="rules-table">
          <thead>
            <tr>
              {/* These header names match the fields in your AnalysisResult entity */}
              <th>Antecedents</th>
              <th>Consequents</th>
              <th>Support</th>
              <th>Confidence</th>
              <th>Lift</th>
            </tr>
          </thead>
          <tbody>
            {/* .map() loops over every rule object and returns a <tr> for each one
                The "key" prop is required by React when rendering lists.
                React uses it internally to track which items changed.
                We use the array index as the key — fine for a read-only table. */}
            {rules.map((rule, index) => (
              <tr key={index}>
                {/* rule.antecedents, rule.consequents, etc. must match
                    the exact JSON field names your Spring Boot backend returns */}
                <td>{rule.antecedents}</td>
                <td>{rule.consequents}</td>

                {/* toFixed(4) formats numbers to 4 decimal places: 0.12345678 → 0.1235
                    We parse it first because the API might return it as a string */}
                <td>{parseFloat(rule.support).toFixed(4)}</td>
                <td>{parseFloat(rule.confidence).toFixed(4)}</td>
                <td>{parseFloat(rule.lift).toFixed(4)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default RulesTable;