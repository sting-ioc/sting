#!/usr/bin/env python3
"""Check links to files within the built documentation site."""

import sys
from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import unquote, urlsplit


class Links(HTMLParser):
    def __init__(self):
        super().__init__()
        self.urls = []

    def handle_starttag(self, tag, attrs):
        for key, value in attrs:
            if key in ("href", "src") and value:
                self.urls.append(value)


def main():
    root = Path(sys.argv[1]).resolve()
    if not root.is_dir():
        raise SystemExit(f"Missing built site: {root}")
    broken = []
    count = 0
    for html in root.rglob("*.html"):
        parser = Links()
        parser.feed(html.read_text(encoding="utf-8"))
        for url in parser.urls:
            parsed = urlsplit(url)
            if parsed.scheme or parsed.netloc or not parsed.path:
                continue
            path = unquote(parsed.path)
            destination = root / path.lstrip("/") if path.startswith("/") else html.parent / path
            destination = destination.resolve()
            if not destination.is_relative_to(root):
                broken.append((html, url))
                continue
            if destination.is_dir():
                destination /= "index.html"
            if not destination.exists() and not destination.suffix:
                destination = destination.with_suffix(".html")
            count += 1
            if not destination.exists():
                broken.append((html, url))
    for source, url in broken[:50]:
        print(f"{source.relative_to(root)}: {url}")
    print(f"Checked {count} local links; {len(broken)} broken")
    if broken:
        raise SystemExit(1)


if __name__ == "__main__":
    main()
