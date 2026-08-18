import { NextResponse } from "next/server";
import { getById, update, remove } from "../../../../lib/widgets-store";

export async function GET(request, { params }) {
  const { id } = await params;
  const widget = getById(id);
  if (!widget) {
    return NextResponse.json({ error: `No widget with id ${id}` }, { status: 404 });
  }
  return NextResponse.json(widget);
}

export async function PATCH(request, { params }) {
  const { id } = await params;
  let body;
  try {
    body = await request.json();
  } catch {
    return NextResponse.json({ error: "Body must be valid JSON" }, { status: 400 });
  }

  const widget = update(id, body);
  if (!widget) {
    return NextResponse.json({ error: `No widget with id ${id}` }, { status: 404 });
  }
  return NextResponse.json(widget);
}

export async function DELETE(request, { params }) {
  const { id } = await params;
  const removed = remove(id);
  if (!removed) {
    return NextResponse.json({ error: `No widget with id ${id}` }, { status: 404 });
  }
  return new NextResponse(null, { status: 204 });
}
