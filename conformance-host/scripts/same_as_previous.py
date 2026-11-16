#!/usr/bin/env python3
"""Captures that are byte-identical to the capture taken just before them in
one lane while the other lane drew the two differently: a fixture captured
before its own frame reached the display (ticket
kjui-conformance-dynamic-effectstyle-light-captured-before-drawn; run
43684339304 captured common/effectStyle__light as common/tintColor__binding).

Many neighbours are rightly identical (370 of 1009 pairs in that run), so a
pair counts only when the other lane tells the two apart.

  same_as_previous.py <results.json> <lane dir> <other lane dir>

The capture order is the results' order. Prints each pair and the count of
pairs compared; exit 1 when a pair is found, 2 when nothing could be compared.
"""
import hashlib
import json
import os
import sys


def digest(directory, shot):
    path = os.path.join(directory, os.path.basename(shot))
    if not os.path.exists(path):
        return None
    with open(path, "rb") as f:
        return hashlib.sha1(f.read()).hexdigest()


def main(argv):
    results, lane, other = argv[1], argv[2], argv[3]
    with open(results) as f:
        rows = json.load(f)["results"]
    shots = [(r["id"], r["screenshot"]) for r in rows if r.get("screenshot")]
    compared = found = 0
    for (prev_id, prev), (cur_id, cur) in zip(shots, shots[1:]):
        a, b = digest(lane, prev), digest(lane, cur)
        oa, ob = digest(other, prev), digest(other, cur)
        if None in (a, b, oa, ob):
            continue
        compared += 1
        if a == b and oa != ob:
            found += 1
            print(f"same as previous: {prev_id} -> {cur_id}")
    print(f"{found} of {compared} neighbouring pairs ({len(shots)} captures)")
    if compared == 0:
        return 2
    return 1 if found else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
