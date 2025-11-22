import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import { execSync } from 'child_process';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const packageJsonPath = path.join(__dirname, '../package.json');

// Read package.json
console.log(`Reading package.json from ${packageJsonPath}...`);
const packageJsonContent = fs.readFileSync(packageJsonPath, 'utf-8');
const packageJson = JSON.parse(packageJsonContent);

// Calculate new version
const now = new Date();
const year = now.getFullYear().toString().slice(-2);
const month = (now.getMonth() + 1).toString().padStart(2, '0');
const day = now.getDate().toString().padStart(2, '0');

const dateString = `${year}-${month}-${day}`;
// Assuming version is like 0.0.0 or 0.0.0-something. We want to keep the base 0.0.0
const baseVersion = packageJson.version.split('-')[0]; 
const newVersion = `${baseVersion}-${dateString}`;

console.log(`Current version: ${packageJson.version}`);
console.log(`New version: ${newVersion}`);

// Update package.json
packageJson.version = newVersion;
fs.writeFileSync(packageJsonPath, JSON.stringify(packageJson, null, 2) + '\n');

try {
    const buildCommand = process.argv[2] || 'npm run electron:build:win';
    console.log(`Starting build process (${buildCommand})...`);
    
    // Execute the build command
    execSync(buildCommand, {
        cwd: path.join(__dirname, '..'),
        stdio: 'inherit'
    });

    console.log('---------------------------------------------------');
    console.log('Build completed successfully!');
    console.log(`Version: ${newVersion}`);
    
    // Check for output directory (based on package.json configuration)
    const outputDir = packageJson.build?.directories?.output || 'dist';
    const fullOutputPath = path.join(__dirname, '..', outputDir);
    
    console.log(`Build artifacts should be located in: ${fullOutputPath}`);
    console.log('---------------------------------------------------');

} catch (error) {
    console.error('Build failed!');
    // Restore original version in case of failure? 
    // For now, we leave it as is or the user can revert via git.
    process.exit(1);
}