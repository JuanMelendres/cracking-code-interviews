// Minimal real DOM manipulation, referenced by
// syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md
// to show what React abstracts away: direct querySelector + imperative
// mutation of the live DOM tree (not the original HTML text).

document.addEventListener("DOMContentLoaded", () => {
  const header = document.querySelector(".site-header h1");
  if (header) {
    header.setAttribute("data-loaded", "true");
  }

  const cards = document.querySelectorAll(".card");
  cards.forEach((card, index) => {
    card.dataset.cardIndex = String(index);
  });
});
