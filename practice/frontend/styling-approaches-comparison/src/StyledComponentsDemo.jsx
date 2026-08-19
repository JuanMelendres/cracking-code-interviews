import styled from "styled-components";

// F-302: `$urgent` drives the ACTUAL CSS rule (background color) at
// RUNTIME, based on a prop value -- something CSS Modules/Tailwind (both
// build-time, static class-name approaches) cannot do without either
// pre-generating every variant as its own class or falling back to an
// inline style. This is the real, distinguishing capability CSS-in-JS
// trades startup/runtime cost for.
const Badge = styled.span`
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  color: white;
  background: ${(props) => (props.$urgent ? "crimson" : "seagreen")};
`;

export default function StyledComponentsDemo() {
  return (
    <div>
      <h2>CSS-in-JS (styled-components)</h2>
      <Badge data-testid="badge-normal">Normal</Badge>{" "}
      <Badge $urgent data-testid="badge-urgent">Urgent</Badge>
    </div>
  );
}
