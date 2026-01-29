import UrlShortener from "./components/UrlShortener";
import "./App.css";
import { useState } from "react";
import UrlStats from "./components/UrlStats";

function App() {
  const [activeTab, setActiveTab] = useState<"create" | "stats">("create");
  return (
    <div className="App">
      <div className="tabs">
        <button
          className={activeTab === "create" ? "tab active" : "tab"}
          onClick={() => setActiveTab("create")}
        >
          Creeaza URL
        </button>

        <button
          className={activeTab === "stats" ? "tab active" : "tab"}
          onClick={() => setActiveTab("stats")}
        >
          Statistici
        </button>
      </div>

      {activeTab === "create" && <UrlShortener />}
      {activeTab === "stats" && <UrlStats />}
    </div>
  );
}

export default App;
