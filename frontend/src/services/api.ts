import axios from "axios";

const api = axios.create({
  baseURL: "/api", //foloseste proxy din vite.config.js
  headers: {
    "Content-Type": "application/json",
  },
});

//creeaza un URL scurtat
export const createShortUrl = async (originalUrl: string) => {
  try {
    const response = await api.post("/urls", { originalUrl: originalUrl });
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message;
  }
};

//obtine detalii despre url
export const getUrlDetails = async (shortCode: string) => {
  try {
    const response = await api.get(`/urls/${shortCode}`);
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message;
  }
};
