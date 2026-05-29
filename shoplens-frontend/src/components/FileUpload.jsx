// FileUpload.jsx
// Handles file selection and the upload action
// Props:
//   onUpload → a function passed from App.jsx that triggers the API call

import { useState } from "react";

function FileUpload({ onUpload }) {
  // selectedFile holds the File object the user picks from their computer
  // null means no file is selected yet
  const [selectedFile, setSelectedFile] = useState(null);

  // This function runs whenever the user picks a file using the input
  function handleFileChange(event) {
    // event.target.files is an array-like list of selected files
    // We take [0] because we only allow one file at a time
    const file = event.target.files[0];
    setSelectedFile(file);
  }

  // This function runs when the user clicks the "Analyze" button
  function handleSubmit() {
    // Guard: if no file was selected, don't proceed
    if (!selectedFile) {
      alert("Please select a CSV file first.");
      return;
    }

    // FormData is a built-in browser class that packages data
    // for sending as multipart/form-data — exactly what your
    // Spring Boot backend expects for file uploads
    const formData = new FormData();

    // "file" must match the @RequestParam("file") name in your
    // Spring Boot controller exactly
    formData.append("file", selectedFile);

    // Call the function passed down from App.jsx
    // App.jsx handles the actual fetch() call
    onUpload(formData);
  }

  return (
    <div className="upload-card">
      <h2 className="upload-title">Upload Transaction Data</h2>
      <p className="upload-subtitle">
        Upload a CSV file with transaction data to discover association rules
      </p>

      <div className="upload-row">
        {/* The actual file picker input.
            "accept" restricts the file browser to only show .csv files.
            onChange fires whenever the user selects a file. */}
        <label className="file-label">
          <input
            type="file"
            accept=".csv"
            onChange={handleFileChange}
            className="file-input"
          />
          {/* This span is what the user visually sees.
              The real <input type="file"> is hidden with CSS. */}
          <span className="file-button">📂 Choose CSV File</span>
        </label>

        {/* Show the selected filename, or a prompt if nothing is selected yet */}
        <span className="file-name">
          {selectedFile ? selectedFile.name : "No file chosen"}
        </span>
      </div>

      {/* The Analyze button triggers the upload */}
      <button
        className="analyze-button"
        onClick={handleSubmit}
      >
        🔍 Analyze
      </button>
    </div>
  );
}

export default FileUpload;