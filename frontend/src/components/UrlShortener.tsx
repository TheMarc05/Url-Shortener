import { ChangeEvent, SyntheticEvent, useState } from "react";
import { ApiError, UrlResponse } from "../types";
import { createShortUrl } from "../services/api";
import UrlDisplay from "./UrlDisplay";
import "./UrlShortener.css";

const UrlShortener = () => {
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [originalUrl, setOriginalUrl] = useState<string>("");
  const [shortUrlData, setShortUrlData] = useState<UrlResponse | null>(null);

  const handleSubmit = async (
    e: SyntheticEvent<HTMLFormElement>,
  ): Promise<void> => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const data = await createShortUrl(originalUrl);
      setShortUrlData(data);
      setOriginalUrl(""); //input gol dupa submit
    } catch (error) {
      const apiError = error as ApiError;
      setError(
        apiError.message || "A aparut o eroare. Te rugam sa incerci din nou",
      );
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>): void => {
    setOriginalUrl(e.target.value);
  };

  return (
    <div className="url-shortener-container">
      <h1>URL Shortener</h1>
      <p className="subtitle">
        Transforma URL-uri lungi in link-uri scurrte si usor de partajat
      </p>

      <form onSubmit={handleSubmit} className="url-form">
        <div className="input-group">
          <input
            type="url"
            value={originalUrl}
            onChange={handleInputChange}
            placeholder="Introdu URL-ul de scurtat"
            required
            disabled={loading}
            className="url-input"
          />
          <button
            type="submit"
            disabled={loading || !originalUrl}
            className="submit-button"
          >
            {loading ? "Se proceseaza..." : "Scurteaza URL"}
          </button>
        </div>
      </form>

      {error && <div className="error-message">{error}</div>}

      {shortUrlData && <UrlDisplay urlData={shortUrlData} />}
    </div>
  );
};

export default UrlShortener;
