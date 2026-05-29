// ErrorMessage.jsx
// Displays a styled error box when something goes wrong
// Receives the error text as a prop called "message"

function ErrorMessage({ message }) {
  // If no message is passed in, render nothing at all
  // This is called "conditional rendering"
  if (!message) return null;

  return (
    <div className="error-box">
      {/* ⚠️ is a unicode emoji — works fine in JSX */}
      <span className="error-icon">⚠️</span>
      <p className="error-text">{message}</p>
    </div>
  );
}

export default ErrorMessage;