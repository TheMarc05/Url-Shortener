import { useState } from "react";
import { UrlResponse } from "../types";
import "./UrlDisplay.css";

interface UrlDisplayProps {
  urlData: UrlResponse;
}

const UrlDisplay = ({ urlData }: UrlDisplayProps) => {
  const [copied, setCopied] = useState<boolean>(false);

  const handleCopy = (): void => {
    navigator.clipboard.writeText(urlData.shortUrl);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const formatDate = (dateString: string): string => {
    return new Date(dateString).toLocaleDateString("ro-RO", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  return (
    <div className="url-display-container">
      <h2>URL-ul tau scurtat:</h2>
      <div className="url-box">
        <input
          type="text"
          value={urlData.shortUrl}
          readOnly
          className="short-url-input"
        />
        <button onClick={handleCopy} className="copy-button">
          {copied ? "Copiat!" : "Copiaza"}{" "}
        </button>
      </div>

      <div className="url-info">
        <p>
          <strong>URL original:</strong> {urlData.originalUrl}{" "}
        </p>
        <p>
          <strong>Cod scurt:</strong> {urlData.shortCode}{" "}
        </p>
        <p>
          <strong>Click-uri:</strong> {urlData.clickCount}{" "}
        </p>
        {urlData.createdAt && (
          <p>
            <strong>Creat la:</strong> {formatDate(urlData.createdAt)}
          </p>
        )}
      </div>

      <div className="test-link">
        <a href={urlData.shortUrl} target="_blank" rel="noopener noreferrer">
          Testeaza link-ul
        </a>
      </div>
    </div>
  );
};

export default UrlDisplay;
