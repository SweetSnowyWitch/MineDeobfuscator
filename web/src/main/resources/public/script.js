const input = document.getElementById("input");
const urlInput = document.getElementById("urlinput");

const status = document.getElementById("status");
const message = document.getElementById("message");

const copyButton = document.getElementById("copy");
const deobfButton = document.getElementById("deobfuscate");

const mappingsSelect = document.getElementById("mappings");
const versionSelect = document.getElementById("version");
const environmentSelect = document.getElementById("environment");

function loadVersions() {
    const req = new XMLHttpRequest();
    req.open("GET", "/mc_versions.json", true);
    req.onreadystatechange = () => {
        if (req.readyState != 4) {
            return;
        }

        const versions = JSON.parse(req.responseText);
        versions.forEach((version, index) => {
            console.log(version);
            const option = document.createElement("option");
            option.value = version.world_version;
            option.innerText = version.name;
            versionSelect.appendChild(option);
        });
        versionSelect.remove(0);
        console.log(`Added ${versions.length} possible minecraft versions`)
    };

    req.setRequestHeader("Accept", "application/json");
    req.send();
}

function deobfuscate() {
    const mappings = mappingsSelect.options[mappingsSelect.selectedIndex].value;
    const version = versionSelect.options[versionSelect.selectedIndex].value;
    const environment = environmentSelect.options[environmentSelect.selectedIndex].value;

    status.innerText = "Deobfuscating...";
    message.innerText = "";

    input.disabled = true;
    urlInput.disabled = true;
    copyButton.disabled = true;
    deobfButton.disabled = true;
    mappingsSelect.disabled = true;
    versionSelect.disabled = true;
    environmentSelect.disabled = true;

    const req = new XMLHttpRequest();
    req.onreadystatechange = () => {
        if (req.readyState != 4) {
            return;
        }

        input.disabled = false;
        urlInput.disabled = false;
        copyButton.disabled = false;
        deobfButton.disabled = false;
        mappingsSelect.disabled = false;
        versionSelect.disabled = false;
        environmentSelect.disabled = false;

        const totalTime = (req.getResponseHeader("Total-Time") / 1000000).toFixed(2);

        if (req.status != 200) {
            status.innerText = `Error ${req.status} while deobfuscating${totalTime > 0 ? ` (took ${totalTime}ms)` : ""}:`;
            message.innerText = req.responseText;
            return;
        }

        const mappingsTime = (req.getResponseHeader("Mappings-Time") / 1000000).toFixed(2);
        const remapTime = (req.getResponseHeader("Remap-Time") / 1000000).toFixed(2);

        status.innerText = `Deobfuscating took ${totalTime}ms (${mappingsTime}ms creating mappings, ${remapTime}ms remapping)`;
        input.value = req.responseText;
    };

    const setHeaders = () => {
        // these require an open request, so just do this
        req.setRequestHeader("Content-Type", "text/plain");
        req.setRequestHeader("Accept", "text/plain");
    };

    const reqParams = `mappings=${mappings}&version=${version}&environment=${environment}`;
    if (urlInput.value.length == 0) {
        req.open("POST", `/api/v1/deobf/body?${reqParams}`, true);
        setHeaders();
        req.send(input.value);
    } else {
        const url = encodeURIComponent(urlInput.value);
        req.open("GET", `/api/v1/deobf/url?${reqParams}&url=${url}`, true);
        setHeaders();
        req.send();
    }
}

copyButton.onclick = () => {
    input.focus();
    input.select();

    if (navigator.clipboard) {
        navigator.clipboard.writeText(input.value);
    } else {
        // fallback
        document.execCommand("copy");
    }
};

loadVersions();
deobfButton.onclick = (event) => deobfuscate();
