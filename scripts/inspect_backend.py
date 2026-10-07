"""Rebuild the endpoint inventory from the supplied Java controllers; never guesses routes."""
import json, re
from pathlib import Path
root = Path(__file__).resolve().parents[1]
java = root / 'backend/src/main/java/com/example/capstone_3'
routes = []
for file in sorted((java / 'Controller').glob('*.java')):
    text = file.read_text(encoding='utf-8-sig')
    base = re.search(r'@RequestMapping\("([^"]+)"\)', text).group(1)
    for m in re.finditer(r'@(Get|Post|Put|Delete)Mapping\((?:value\s*=\s*)?"([^"]+)"[^\n]*\)\s*public\s+ResponseEntity<[^>]+>\s+(\w+)\((.*?)\)\s*(?:throws[\w\s,]+)?\{', text, re.S):
        verb, suffix, method, args = m.groups()
        routes.append(dict(method=verb.upper(), path=base+suffix, handler=method, controller=file.name, arguments=args.strip()))
(root / 'docs/endpoints.json').write_text(json.dumps(routes, indent=2), encoding='utf-8')
(root / 'docs/API-CONTRACT.md').write_text('# Uploaded backend API inventory\n\nGenerated from controller source before frontend implementation. See Java DTOs and models for exact validation constraints.\n\n| Method | Path | Handler |\n|---|---|---|\n' + '\n'.join(f"| {r['method']} | `{r['path']}` | {r['controller']} · {r['handler']} |" for r in routes), encoding='utf-8')
print(f'Inventoried {len(routes)} controller endpoints.')
