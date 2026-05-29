// App.jsx
// The root component of ShopLens
// Owns all state and passes data/functions down to child components

import { useState, useEffect } from "react";
import FileUpload from "./components/FileUpload";
import RulesTable from "./components/RulesTable";
import LoadingSpinner from "./components/LoadingSpinner";
import ErrorMessage from "./components/ErrorMessage";
import "./App.css";

// Your Spring Boot backend URL.
// During development it runs on port 8080.
// You'll change this to your Render URL when you deploy.
const API_BASE = "http://localhost:8080";

function App() {
  // uploadedRules: the rules returned from the latest CSV upload
  const [uploadedRules, setUploadedRules] = useState([]);

  // historyRules: the rules fetched from GET /api/history
  const [historyRules, setHistoryRules] = useState([]);

  // loading: true while waiting for any API response
  const [loading, setLoading] = useState(false);

  // error: holds an error message string, or "" if no error
  const [error, setError] = useState("");

  // showHistory: controls whether the history section is visible
  const [showHistory, setShowHistory] = useState(false);

  // ─── Upload Handler ────────────────────────────────────────────────────────
  // This function is passed DOWN to FileUpload as the "onUpload" prop
  // FileUpload calls it with a FormData object containing the CSV

  async function handleUpload(formData) {
    // Reset previous results and errors before starting
    setError("");
    setUploadedRules([]);
    setLoading(true); // show the spinner

    try {
      const response = await fetch(`${API_BASE}/api/upload`, {
        method: "POST",
        // Do NOT set Content-Type header manually when sending FormData!
        // The browser sets it automatically to multipart/form-data
        // and includes the "boundary" string that Spring Boot needs to parse it.
        body: formData,
      });

      // response.ok is true for status codes 200–299
      // If the server returned a 400 or 500, we treat it as an error
      if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
      }

      // Parse the JSON response body into a JavaScript array
      const data = await response.json();
      setUploadedRules(data); // triggers re-render with the new rules

    } catch (err) {
      // Network errors (backend not running) or non-OK responses land here
      setError(
        err.message === "Failed to fetch"
          ? "Cannot reach the server. Is your Spring Boot app running?"
          : err.message
      );
    } finally {
      // finally always runs — even if there was an error
      // We always want to hide the spinner when the request is done
      setLoading(false);
    }
  }

  // ─── History Handler ───────────────────────────────────────────────────────

  async function handleViewHistory() {
    setError("");
    setLoading(true);

    try {
      const response = await fetch(`${API_BASE}/api/history`);

      if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
      }

      const data = await response.json();
      setHistoryRules(data);
      setShowHistory(true); // reveal the history section

    } catch (err) {
      setError(
        err.message === "Failed to fetch"
          ? "Cannot reach the server. Is your Spring Boot app running?"
          : err.message
      );
    } finally {
      setLoading(false);
    }
  }

  // ─── Render ─────────────────────────────────────────────────────────────────

  return (
    <div className="app-wrapper">
      {/* ── Header ── */}
      <header className="app-header">
        <div className="header-content">
          <h1 className="app-title">🛍️ ShopLens</h1>
          <p className="app-subtitle">
            Market Basket Analysis — Discover what products customers buy together
          </p>
        </div>
      </header>

      {/* ── Main Content ── */}
      <main className="app-main">

        {/* FileUpload receives handleUpload as a prop called "onUpload" */}
        <FileUpload onUpload={handleUpload} />

        {/* Show spinner while loading, but nothing else */}
        {loading && <LoadingSpinner />}

        {/* ErrorMessage receives the error string as "message"
            Returns null silently when error is "" */}
        <ErrorMessage message={error} />

        {/* Show upload results only if we have rules AND we're not loading */}
        {!loading && uploadedRules.length > 0 && (
          <RulesTable
            rules={uploadedRules}
            title="📊 Analysis Results"
          />
        )}

        {/* History section */}
        <div className="history-section">
          <button
            className="history-button"
            onClick={handleViewHistory}
            disabled={loading} // disable button while a request is in flight
          >
            🕓 View All History
          </button>

          {/* Show history table only after the button has been clicked */}
          {!loading && showHistory && (
            <RulesTable
              rules={historyRules}
              title="📁 All Past Results"
            />
          )}
        </div>

      </main>

      {/* ── Footer ── */}
      <footer className="app-footer">
        <p>ShopLens — Built with React + Spring Boot + Python</p>
      </footer>
    </div>
  );
}

export default App;