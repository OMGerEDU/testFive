#!/usr/bin/env python3
"""
iconiq_scraper.py – Convert every public page in iconiqmedia.co.il
into plain-text files suitable for an AI knowledge-base.

Run:
    python iconiq_scraper.py
"""

import os, re, time, json, zipfile, concurrent.futures as cf
import requests, xml.etree.ElementTree as ET
from urllib.parse import urlparse
from bs4 import BeautifulSoup
from readability import Document
from tqdm import tqdm

ROOT_URL = "https://iconiqmedia.co.il"
SITEMAP_INDEX = f"{ROOT_URL}/sitemap_index.xml"
HEADERS = {"User-Agent": "IconiqScraper/1.0 (+https://example.com)"}
OUT_DIR = "output"
ZIP_NAME = "iconiqmedia_texts.zip"
CRAWL_DELAY = 1.0        # seconds between requests
MAX_WORKERS = 10         # tune if you have lots of bandwidth / CPU

os.makedirs(OUT_DIR, exist_ok=True)

def fetch(url, binary=False):
    r = requests.get(url, headers=HEADERS, timeout=20)
    r.raise_for_status()
    return r.content if binary else r.text

def parse_sitemap(xml_text):
    root = ET.fromstring(xml_text)
    ns = {"sm": "http://www.sitemaps.org/schemas/sitemap/0.9"}
    tag = "loc"
    # sitemap index → nested sitemaps
    if root.tag.endswith("sitemapindex"):
        return [el.text.strip() for el in root.findall(".//sm:loc", ns)]
    # single sitemap → urls
    return [el.text.strip() for el in root.findall(".//sm:url/sm:loc", ns)]

def clean_html(html, url):
    try:
        article = Document(html).summary()
        text = BeautifulSoup(article, "lxml").get_text(" ", strip=True)
    except Exception:
        soup = BeautifulSoup(html, "lxml")
        # heuristic: drop nav / footer / script / style
        for tag in soup(["nav", "footer", "script", "style"]):
            tag.extract()
        text = soup.get_text(" ", strip=True)
    text = re.sub(r"\s{2,}", " ", text)
    return text

def scrape_page(url):
    try:
        html = fetch(url)
        text = clean_html(html, url)
        slug = (urlparse(url).path.strip("/") or "home").replace("/", "_")
        fname = f"{slug}.txt"
        with open(os.path.join(OUT_DIR, fname), "w", encoding="utf-8") as f:
            f.write(text)
        return {"url": url, "file": fname}
    except Exception as e:
        return {"url": url, "error": str(e)}

def main():
    print("Fetching sitemap index…")
    nested = parse_sitemap(fetch(SITEMAP_INDEX))
    print(f"Found {len(nested)} nested sitemaps")

    all_urls = []
    for sm in nested:
        try:
            all_urls += parse_sitemap(fetch(sm))
        except Exception as e:
            print(f"⚠️  Skipped {sm}: {e}")

    print(f"Total pages to crawl: {len(all_urls)}")

    results = []
    with cf.ThreadPoolExecutor(max_workers=MAX_WORKERS) as ex:
        futures = {ex.submit(scrape_page, u): u for u in all_urls}
        for fut in tqdm(cf.as_completed(futures), total=len(futures)):
            results.append(fut.result())
            time.sleep(CRAWL_DELAY)

    # write master index
    with open(os.path.join(OUT_DIR, "pages.json"), "w", encoding="utf-8") as f:
        json.dump(results, f, ensure_ascii=False, indent=2)

    # zip everything
    with zipfile.ZipFile(ZIP_NAME, "w", zipfile.ZIP_DEFLATED) as z:
        for rec in results:
            if "file" in rec:
                z.write(os.path.join(OUT_DIR, rec["file"]), arcname=rec["file"])
        z.write(os.path.join(OUT_DIR, "pages.json"), arcname="pages.json")

    print(f"\nDone! Text files in '{OUT_DIR}/', archive: '{ZIP_NAME}'")

if __name__ == "__main__":
    main()
