let contadorEtapas = 0;

window.onload = async function() {
    const token = getToken();

    if (!token) {
        alert('Acesso negado! Faça login.');
        window.location.href = '/login';
        return;
    }

    try {
        const payloadBase64Url = token.split('.')[1];
        const payloadDecodificado = JSON.parse(atob(payloadBase64Url));
        const permissao = String(payloadDecodificado.role || payloadDecodificado.perfil || payloadDecodificado.authorities || '').toUpperCase();

        if (!permissao.includes('ADMIN')) {
            alert('Acesso negado!');
            window.location.href = '/menu';
            return;
        }
    } catch (e) {
        window.location.href = '/login';
        return;
    }

    await carregarDocumentos();

    document.getElementById('formFluxo').addEventListener('submit', salvarFluxo);
};

function getToken() { return localStorage.getItem('meuTokenJWT'); }

async function carregarDocumentos() {
    try {
        const resposta = await fetch('/api/documentos', {
            headers: { 'Authorization': `Bearer ${getToken()}` }
        });

        if (resposta.ok) {
            const documentos = await resposta.json();
            const select = document.getElementById('selectDocumento');

            // Aqui é crucial ter a tag <option>
            select.innerHTML = '<option value="">Selecione um documento...</option>';

            documentos.forEach(doc => {
                select.innerHTML += `<option value="${doc.id}">${doc.nome}</option>`;
            });
        }
    } catch (erro) {
        console.error("Erro ao carregar documentos", erro);
    }
}

function adicionarEtapa() {
    contadorEtapas++;
    const div = document.createElement('div');
    div.className = 'etapa-box';
    div.id = `etapa-${contadorEtapas}`;

    div.innerHTML = `
        <span>Passo:</span>
        <select class="perfil-etapa" required>
            <option value="">Quem precisa conferir?</option>
            <option value="SECRETARIA">Secretaria</option>
            <option value="COORDENADOR">Coordenador</option>
        </select>
        <button type="button" class="btn-remover" onclick="removerEtapa(${contadorEtapas})">X</button>
    `;

    document.getElementById('listaEtapas').appendChild(div);
}

function removerEtapa(id) {
    const elemento = document.getElementById(`etapa-${id}`);
    if (elemento) elemento.remove();
}

async function salvarFluxo(e) {
    e.preventDefault();

    const documentoId = document.getElementById('selectDocumento').value;

    const selectsPerfis = document.querySelectorAll('.perfil-etapa');
    const etapas = Array.from(selectsPerfis).map(select => select.value);

    const dadosFluxo = {
        documentoId: parseInt(documentoId),
        etapas: etapas
    };

    try {
        const resposta = await fetch('/api/fluxos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getToken()}`
            },
            body: JSON.stringify(dadosFluxo)
        });

        if (resposta.ok) {
            alert('Fluxo configurado com sucesso!');
            window.location.reload();
        } else {
            alert('Erro ao salvar fluxo.');
        }
    } catch (erro) {
        alert('Falha de conexão.');
    }
}