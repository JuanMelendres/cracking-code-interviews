import "./index.css";
import CssModulesDemo from "./CssModulesDemo";
import TailwindDemo from "./TailwindDemo";
import StyledComponentsDemo from "./StyledComponentsDemo";

export default function App() {
  return (
    <main style={{ display: "grid", gap: "1.5rem", padding: "1.5rem" }}>
      <h1>Styling Approaches Comparison (F-302)</h1>
      <CssModulesDemo />
      <TailwindDemo />
      <StyledComponentsDemo />
    </main>
  );
}
