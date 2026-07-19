const token = localStorage.getItem('meuTokenJWT');
if (!token) window.location.href = '/login';

const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
};

document.addEventListener('DOMContentLoaded', carregarStatus);

async function carregarStatus() {
    const response = await fetch('/api/status', { headers });
    if (response.ok) {
        const lista = await response.json();
        const tbody = document.getElementById('tabelaStatusBody');
        tbody.innerHTML = '';

        lista.forEach(s => {
            // Formata a visualização de quem é o dono do Status
            const textoPerfil = s.perfilPermitido
                ? `<span style="color: #0056b3; font-weight: bold;">${s.perfilPermitido}</span>`
                : `<span style="color: #6c757d;">Geral</span>`;

            tbody.innerHTML += `
                <tr>
                    <td>#${s.id}</td>
                    <td><strong>${s.nome}</strong></td>
                    <td>${s.finalizaSolicitacao ? '<span style="color: #28a745; font-weight: bold;">Sim</span>' : '<span style="color: #6c757d;">Não</span>'}</td>
                    <td>${textoPerfil}</td>
                    <td><button class="btn-excluir" onclick="deletar(${s.id})">Excluir</button></td>
                </tr>
            `;
        });
    }
}

document.getElementById('formStatus').addEventListener('submit', async (e) => {
    e.preventDefault();
    const nome = document.getElementById('nome').value;
    const finalizaSolicitacao = document.getElementById('finalizaSolicitacao').value === 'true';

    // NOVO: Captura o valor do perfil. Se for vazio (""), enviamos null para o Java.
    const perfilValue = document.getElementById('perfilPermitido').value;
    const perfilPermitido = perfilValue === "" ? null : perfilValue;

    const response = await fetch('/api/status', {
        method: 'POST',
        headers: headers,
        // Incluindo o perfilPermitido no JSON que vai pro back-end
        body: JSON.stringify({ nome, finalizaSolicitacao, perfilPermitido })
    });

    if (response.ok) {
        alert('Status cadastrado com sucesso!');
        document.getElementById('formStatus').reset();
        carregarStatus();
    } else {
        alert('Erro ao cadastrar status.');
    }
});

async function deletar(id) {
    if (!confirm('Deseja realmente excluir este status?')) return;
    const response = await fetch(`/api/status/${id}`, { method: 'DELETE', headers });
    if (response.ok) {
        carregarStatus();
    } else {
        alert('Não é possível excluir um status que está em uso em movimentações ou fluxos.');
    }
}