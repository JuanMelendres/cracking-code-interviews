import { useState } from 'react';

// F-104b: the real, reproducible "index as key" bug.
//
// `key` tells React which DOM node to REUSE across renders for a given list
// item. When key = array index, React reuses node[0] for "whatever is now at
// index 0" -- not for the item that was originally there. An UNCONTROLLED
// <input> (using defaultValue, not value+onChange) keeps its own typed text in
// the DOM node itself, independent of React's re-render -- so if that DOM node
// gets reassigned to a different person after a removal, the typed text stays
// on the node and now appears to belong to the wrong person.
//
// Reproduction: type a note into the FIRST row's input in both lists below,
// then click "Remove first person" on both. The id-keyed list correctly
// removes the note along with the person who had it. The index-keyed list
// does NOT -- the note text stays in the first input, now silently attached
// to a different person's row.

const INITIAL_PEOPLE = [
  { id: 'p1', name: 'Ana' },
  { id: 'p2', name: 'Bilal' },
  { id: 'p3', name: 'Carmen' },
];

function PersonRow({ name }) {
  return (
    <div className="person-row">
      <span className="person-name">{name}</span>
      <input
        type="text"
        placeholder="type a note for this person…"
        defaultValue=""
        data-testid={`note-input-${name}`}
      />
    </div>
  );
}

function PeopleList({ keyedBy }) {
  const [people, setPeople] = useState(INITIAL_PEOPLE);

  return (
    <div className="people-list" data-testid={`list-${keyedBy}`}>
      <h4>Keyed by: {keyedBy}</h4>
      <button type="button" onClick={() => setPeople((p) => p.slice(1))}>
        Remove first person
      </button>
      {people.map((person, index) => (
        // THIS is the entire difference between the two lists:
        <PersonRow key={keyedBy === 'index' ? index : person.id} name={person.name} />
      ))}
    </div>
  );
}

export default function ListKeysPitfall() {
  return (
    <div className="demo-block">
      <h3>F-104b: the index-as-key bug, reproduced live</h3>
      <p>
        Type a note into <strong>Ana's</strong> input in BOTH lists below, then
        click "Remove first person" on both. Watch what happens to the note.
      </p>
      <div className="two-lists">
        <PeopleList keyedBy="index" />
        <PeopleList keyedBy="person.id" />
      </div>
    </div>
  );
}
