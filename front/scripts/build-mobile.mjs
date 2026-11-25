import { cp, mkdir, readFile, rm, writeFile } from 'node:fs/promises';
import { dirname, join, resolve, sep } from 'node:path';
import { fileURLToPath } from 'node:url';

const projectRoot = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const outputDir = resolve(projectRoot, 'mobile');
if (!outputDir.startsWith(projectRoot + sep)) {
  throw new Error('A pasta de saída do app ficou fora do projeto.');
}

await rm(outputDir, { recursive: true, force: true });
await mkdir(outputDir, { recursive: true });
await cp(join(projectRoot, 'index.php'), join(outputDir, 'index.html'));
for (const directory of ['assets', 'css', 'img', 'js']) {
  await cp(join(projectRoot, directory), join(outputDir, directory), { recursive: true });
}

const apiUrl = process.env.FAST_FANTASY_API_URL;
if (!apiUrl || !/^https:\/\//i.test(apiUrl)) {
  throw new Error('Defina FAST_FANTASY_API_URL com o endereço HTTPS público da API antes de gerar o app.');
}
const configPath = join(outputDir, 'js', 'api-config.js');
await writeFile(configPath, `window.API_BASE_URL = ${JSON.stringify(apiUrl.replace(/\/$/, ''))};\n`, 'utf8');

const config = JSON.parse(await readFile(join(projectRoot, 'capacitor.config.json'), 'utf8'));
if (!config.appId || !config.appName || config.webDir !== 'mobile') {
  throw new Error('A configuração do Capacitor está inválida.');
}
console.log(`Front preparado em ${outputDir}`);
