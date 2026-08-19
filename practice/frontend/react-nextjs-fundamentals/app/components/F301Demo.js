"use client";

import { useState } from "react";
import dynamic from "next/dynamic";
import { add } from "../../lib/f301-math-utils";

const F301LazyPanel = dynamic(() => import("./F301LazyPanel"));

export default function F301Demo() {
  const [showLazy, setShowLazy] = useState(false);

  return (
    <div>
      <p data-testid="f301-sum">2 + 3 = {add(2, 3)}</p>
      <button onClick={() => setShowLazy(true)}>Load lazy panel</button>
      {showLazy && <F301LazyPanel />}
    </div>
  );
}
