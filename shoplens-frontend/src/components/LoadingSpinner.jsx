// LoadingSpinner.jsx
// This component renders a spinning circle animation
// It takes no props — it either shows or it doesn't

function LoadingSpinner() {
  return (
    // The outer div centers the spinner on the page
    <div className="spinner-container">
      {/* This div has no content — it's styled with CSS to become a spinning circle */}
      <div className="spinner"></div>
      <p className="spinner-text">Analyzing your data...</p>
    </div>
  );
}

// Every component file must export the function
// so other files can import and use it
export default LoadingSpinner;