// F-111 (composition vs. inheritance): a generic `Panel` specialized for two
// different uses via COMPOSITION (passing different props/children), not by
// creating an AlertPanel-extends-Panel / InfoPanel-extends-Panel class
// hierarchy. React's own docs explicitly recommend this: "we have not found
// any cases where we would recommend creating component inheritance
// hierarchies." Both variants below are the exact same `Panel` component,
// just called with different props -- no subclass exists anywhere.
function Panel({ tone, title, children }) {
  const toneStyles = {
    alert: { borderColor: '#c0392b', background: '#fdecea' },
    info: { borderColor: '#2980b9', background: '#eaf2fa' },
  };
  return (
    <div style={{ border: `2px solid ${toneStyles[tone].borderColor}`, background: toneStyles[tone].background, padding: 10, borderRadius: 6, marginBottom: 8 }}>
      <strong>{title}</strong>
      <div>{children}</div>
    </div>
  );
}

export default function CompositionVsInheritanceDemo() {
  return (
    <div className="demo-block">
      <h3>F-111: composition specializing a generic component (not inheritance)</h3>
      <p>
        Both panels below render the SAME <code>Panel</code> component — no
        <code>AlertPanel extends Panel</code> class hierarchy exists. Specialization
        happens entirely through the props passed at each call site.
      </p>
      <Panel tone="alert" title="Alert">
        Disk usage above 90% — composition, not a subclass.
      </Panel>
      <Panel tone="info" title="Info">
        Deployment completed — same component, different props.
      </Panel>
    </div>
  );
}
