export interface CreateUrlRequest {
  originalUrl: string;
}

export interface UrlResponse {
  shortCode: string;
  shortUrl: string;
  originalUrl: string;
  clickCount: number;
  createdAt: string;
  expiresAt: string;
}

export interface ApiError {
  status: number;
  error: string;
  message: string;
  timestamp: string;
  errors?: Record<string, string>;
}
