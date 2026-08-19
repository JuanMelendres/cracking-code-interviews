import stylesA from "./CssModulesDemo.module.css";
import stylesB from "./CssModulesDemoTwo.module.css";

// F-302: two components, each importing a DIFFERENT .module.css file that
// happens to define the SAME source-level class name (.title). If CSS
// Modules' scoping is real, `stylesA.title` and `stylesB.title` resolve
// to two different, non-colliding compiled class name strings -- proven
// directly by rendering both real string values and inspecting the real
// build output.
export default function CssModulesDemo() {
  return (
    <div className={stylesA.box}>
      <h2 className={stylesA.title}>CSS Modules (A)</h2>
      <p data-testid="class-a">stylesA.title = "{stylesA.title}"</p>
      <p className={stylesB.title} data-testid="class-b-el">CSS Modules (B, different file, same source class name)</p>
      <p data-testid="class-b">stylesB.title = "{stylesB.title}"</p>
    </div>
  );
}
