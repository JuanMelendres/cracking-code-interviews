import { createContext, useContext, useState } from 'react';

// F-111d: compound components -- a DIFFERENT problem than HOC/render-prop
// solve. Those two share stateful BEHAVIOR across otherwise-unrelated
// components. Compound components share IMPLICIT STATE among a fixed set
// of components that are always used TOGETHER, presenting a flexible,
// declarative public API (<Tabs><Tabs.List>...) without the caller ever
// touching the shared state directly -- solved here with plain Context,
// scoped locally to this one compound component, not exported or reused
// elsewhere.

const TabsContext = createContext(null);

function Tabs({ children, defaultTab }) {
  const [activeTab, setActiveTab] = useState(defaultTab);
  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      <div className="tabs-root">{children}</div>
    </TabsContext.Provider>
  );
}

function TabList({ children }) {
  return <div className="tabs-list">{children}</div>;
}

function Tab({ id, children }) {
  const { activeTab, setActiveTab } = useContext(TabsContext);
  const isActive = activeTab === id;
  return (
    <button
      type="button"
      onClick={() => setActiveTab(id)}
      data-testid={`tab-${id}`}
      aria-selected={isActive}
      style={{ fontWeight: isActive ? 'bold' : 'normal' }}
    >
      {children}
    </button>
  );
}

function TabPanel({ id, children }) {
  const { activeTab } = useContext(TabsContext);
  if (activeTab !== id) return null;
  return <div className="tabs-panel" data-testid={`panel-${id}`}>{children}</div>;
}

// Attaching sub-components as properties is the conventional API shape for
// this pattern -- `<Tabs.List>`, `<Tabs.Tab>` read as clearly belonging
// together, without a caller ever importing four separate named exports.
Tabs.List = TabList;
Tabs.Tab = Tab;
Tabs.Panel = TabPanel;

export default function CompoundComponentsDemo() {
  return (
    <div className="demo-block">
      <h3>F-111d: compound components — a real Tabs widget</h3>
      <p>Click each tab. The caller below never touches `activeTab` directly — Tabs manages it, shared implicitly among its children via Context.</p>
      <Tabs defaultTab="profile">
        <Tabs.List>
          <Tabs.Tab id="profile">Profile</Tabs.Tab>
          <Tabs.Tab id="settings">Settings</Tabs.Tab>
          <Tabs.Tab id="billing">Billing</Tabs.Tab>
        </Tabs.List>
        <Tabs.Panel id="profile">Profile panel content.</Tabs.Panel>
        <Tabs.Panel id="settings">Settings panel content.</Tabs.Panel>
        <Tabs.Panel id="billing">Billing panel content.</Tabs.Panel>
      </Tabs>
    </div>
  );
}
