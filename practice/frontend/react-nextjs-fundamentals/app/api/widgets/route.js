import { NextResponse } from "next/server";
import { getAll, add } from "../../../lib/widgets-store";

// No `dynamic` export here -> default behavior. Per this version's own
// docs, GET Route Handlers are NOT cached by default; only POST/PUT/etc.
// exist here at all, and none of them can be cached regardless of config.
export async function GET() {
  return NextResponse.json(getAll());
}

export async function POST(request) {
  let body;
  try {
    body = await request.json();
  } catch {
    return NextResponse.json({ error: "Body must be valid JSON" }, { status: 400 });
  }

  if (typeof body.name !== "string" || body.name.trim() === "" || typeof body.qty !== "number") {
    return NextResponse.json(
      { error: "Body must include a non-empty string 'name' and a numeric 'qty'" },
      { status: 400 },
    );
  }

  const widget = add({ name: body.name, qty: body.qty });
  return NextResponse.json(widget, {
    status: 201,
    headers: { Location: `/api/widgets/${widget.id}` },
  });
}
