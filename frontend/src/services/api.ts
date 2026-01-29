import axios, { AxiosError } from "axios";
import { CreateUrlRequest, UrlResponse, ApiError } from "../types";

const api = axios.create({
  baseURL: "/api", //foloseste proxy din vite.config.js
  headers: {
    "Content-Type": "application/json",
  },
});

//creeaza un URL scurtat
export const createShortUrl = async (
  originalUrl: string,
): Promise<UrlResponse> => {
  try {
    const request: CreateUrlRequest = { originalUrl };
    const response = await api.post<UrlResponse>("/urls", request);
    return response.data;
  } catch (error) {
    const axiosError = error as AxiosError<ApiError>;
    throw axiosError.response?.data || new Error(axiosError.message);
  }
};

//obtine detalii despre url
export const getUrlDetails = async (
  shortCode: string,
): Promise<UrlResponse> => {
  try {
    const response = await api.get<UrlResponse>(`/urls/${shortCode}`);
    return response.data;
  } catch (error) {
    const axiosError = error as AxiosError<ApiError>;
    throw axiosError.response?.data || new Error(axiosError.message);
  }
};

export default api;
