import {readFileSync,readdirSync} from 'node:fs';
import {routes} from '../src/api.js';
const source='../backend/src/main/java/com/example/capstone_3/Controller/';
const known=new Set();
for(const file of readdirSync(source)){if(!file.endsWith('.java'))continue;const text=readFileSync(source+file,'utf8');const base=text.match(/@RequestMapping\("([^"]+)"\)/)?.[1];for(const m of text.matchAll(/@(Get|Post|Put|Delete)Mapping\((?:value\s*=\s*)?"([^"]+)"/g))known.add(`${m[1].toUpperCase()} ${base}${m[2]}`);}
let checked=0;for(const [key,[method,path]] of Object.entries(routes)){if(!known.has(`${method} ${path}`))throw new Error(`Unknown backend route: ${key} ${method} ${path}`);checked++;}
const admin={account:['add','update','delete'],'individual-profile':['add','update','delete'],'company-profile':['add','update','delete'],skill:['add','update','delete'],'account-skill':['update','delete'],'skill-offer':['update','delete'],'learning-request':['update','delete'],'request-negotiation':['update','delete'],exchange:['update','delete'],agreement:['add','update','delete'],session:['add','update','delete'],'session-participant':['add','update','delete'],'skill-assessment':[],review:['update','delete'],'token-transaction':['add','update','delete']};
for(const [resource,actions] of Object.entries(admin)){for(const action of ['get',...actions]){const method=action==='get'?'GET':action==='add'?'POST':action==='update'?'PUT':'DELETE';const path=`/api/v1/${resource}/${action}${['update','delete'].includes(action)?'/{id}':''}`;if(!known.has(`${method} ${path}`))throw new Error(`Unknown admin route ${path}`);checked++;}}
console.log(`${checked} frontend and admin method/path contracts match ${known.size} uploaded backend endpoints.`);
