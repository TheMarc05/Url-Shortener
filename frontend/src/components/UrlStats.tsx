import { ChangeEvent, SyntheticEvent, useState } from "react";
import { getUrlDetails } from "../services/api";
import { ApiError, UrlResponse } from "../types";
import "./UrlStats.css";

const UrlStats = () => {
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [shortCode, setShortCode] = useState<string>("");
  const [urlData, setUrlData] = useState<UrlResponse | null>(null);
  const handleSubmit = async (
    e: SyntheticEvent<HTMLFormElement>,
  ): Promise<void> => {
    e.preventDefault();
    await fetchStats(shortCode);
  };

  //reincarca statisticile pentru un cod scurt dat
  const fetchStats = async (code: string): Promise<void> => {
    setError(null);
    setLoading(true);

    try {
      const data = await getUrlDetails(code.trim());
      setUrlData(data);
    } catch (err) {
      const apiError = err as ApiError;
      setError(
        apiError.message || "A aparut o eroare. Te rugam sa incerci din nou",
      );
      setUrlData(null);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>): void => {
    setShortCode(e.target.value);
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
    <div className="url-stats-container">
      <h2>Statistici URL</h2>
      <p className="subtitle">Introdu codul URL pentru a vedea statistici</p>

      <form onSubmit={handleSubmit} className="stats-form">
        <div className="input-group">
          <input
            type="text"
            value={shortCode}
            onChange={handleInputChange}
            placeholder="Introdu codul scurt"
            required
            disabled={loading}
            className="code-input"
          />
          <button
            type="submit"
            disabled={loading || !shortCode}
            className="submit-button"
          >
            {loading ? "Se cauta.." : "Cauta statistici"}{" "}
          </button>
        </div>
      </form>

      {error && <div className="error-message">{error}</div>}

      {urlData && (
        <div className="stats-display">
          <h3>Statistici pentru: {urlData.shortCode}</h3>

          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-label">URL scurtat</div>
              <div className="stat-value">{urlData.shortUrl}</div>
            </div>

            <div className="stat-card">
              <div className="stat-label">URL original</div>
              <div className="stat-value-url">{urlData.originalUrl}</div>
            </div>

            <div className="stat-card highlight">
              <div className="stat-label">Click-uri totale</div>
              <div className="stat-value-large">{urlData.clickCount}</div>
            </div>

            <div className="stat-card">
              <div className="stat-label">Creat la</div>
              <div className="stat-value">{formatDate(urlData.createdAt)}</div>
            </div>

            {urlData.expiresAt && (
              <div className="stat-card">
                <div className="stat-label">Expiră la</div>
                <div className="stat-value">
                  {formatDate(urlData.expiresAt)}
                </div>
              </div>
            )}
          </div>

          <div className="actions">
            <a
              href={urlData.shortUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="test-button"
            >
              Testeaza link-ul
            </a>
            <button
              type="button"
              onClick={() => fetchStats(urlData.shortCode)}
              disabled={loading}
              className="refresh-button"
            >
              {loading ? "Se actualizeaza..." : "Actualizeaza statisticile"}
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default UrlStats;
