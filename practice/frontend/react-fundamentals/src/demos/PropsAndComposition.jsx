// F-102: props flow one direction, parent -> child, and are read-only from the
// child's perspective. Composition (passing `children`) is how React expresses
// "wrap this content" without the child needing to know what it's wrapping.

function Avatar({ initials, color }) {
  return (
    <span
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        width: 32,
        height: 32,
        borderRadius: '50%',
        background: color,
        color: 'white',
        fontSize: 12,
        marginRight: 8,
      }}
    >
      {initials}
    </span>
  );
}

// Card doesn't know or care what's inside it -- it just renders `children`.
// This is composition: Card is reusable for an Avatar, a paragraph, a button,
// anything -- without Card ever importing those components.
function Card({ title, children }) {
  return (
    <div className="fake-card" data-testid="card">
      <h4>{title}</h4>
      <div className="fake-card-body">{children}</div>
    </div>
  );
}

export default function PropsAndComposition() {
  return (
    <div className="demo-block">
      <h3>F-102: Props (parent → child) and composition (children)</h3>
      <Card title="Composed via children">
        <Avatar initials="JM" color="#4f46e5" />
        <span>Card received this whole block as `children` — it never imported Avatar.</span>
      </Card>
      <Card title="Same Card, different children">
        <button type="button">A button, also just `children`</button>
      </Card>
    </div>
  );
}
