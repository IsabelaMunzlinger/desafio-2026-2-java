const token = localStorage.getItem('meuTokenJWT');

if (!token) {
    window.location.href = '/login';
}

const authHeaders = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
};

let listaDeStatus = [];

document.addEventListener('DOMContentLoaded', async () => {
    await carregarOpcoesDeStatus();
    await carregarFila();
});

async function carregarOpcoesDeStatus() {
    try {
        const response = await fetch('/api/status/permitidos', { headers: authHeaders });
        if (response.ok) {
                    const todosOsStatus = await response.json();
                    listaDeStatus = todosOsStatus.filter(s => {
                        const nomeLimpo = s.nome.toUpperCase();
                        return nomeLimpo !== 'EMITIDO';
                    });
        }
    } catch (error) {
        console.error("Erro ao carregar opções de status", error);
    }
}

async function carregarFila() {
    try {
        const response = await fetch('/api/solicitacoes/fila-trabalho', { headers: authHeaders });

        if (response.ok) {
            const pedidos = await response.json();
            const tabelaBody = document.getElementById('tabelaFilaBody');
            tabelaBody.innerHTML = '';

            const optionsHTML = listaDeStatus.map(s => `<option value="${s.id}">${s.nome}</option>`).join('');

            pedidos.forEach(pedido => {
                tabelaBody.innerHTML += `
                    <tr>
                        <td>${pedido.id}</td>
                        <td>${pedido.aluno.nome}</td>
                        <td>${pedido.tipo.nome}</td>
                        <td><strong>${pedido.status.nome}</strong></td>
                        <td style="color: ${pedido.prioridade === 'URGENTE' ? 'red' : 'black'};">${pedido.prioridade}</td>
                        <td>
                            <select id="novoStatus_${pedido.id}" style="padding: 5px;">
                                <option value="">Escolha a decisão...</option>
                                ${optionsHTML}
                            </select>
                            <input type="text" id="obs_${pedido.id}" placeholder="Observação (opcional)">
                            <button class="btn-salvar" onclick="atualizarStatus(${pedido.id})">Confirmar</button>
                        </td>
                    </tr>
                `;
            });
        }
    } catch (error) {
        console.error("Erro ao carregar fila:", error);
    }
}

async function atualizarStatus(solicitacaoId) {
    const selectBox = document.getElementById(`novoStatus_${solicitacaoId}`);
    const novoStatusId = selectBox.value;
    const observacao = document.getElementById(`obs_${solicitacaoId}`).value;

    if (!novoStatusId) {
        alert("Por favor, selecione uma decisão no menu.");
        return;
    }

    const dto = {
        novoStatusId: parseInt(novoStatusId),
        observacao: observacao
    };

    try {
        const response = await fetch(`/api/solicitacoes/${solicitacaoId}/status`, {
            method: 'PUT',
            headers: authHeaders,
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            alert("Análise registrada! O documento seguiu o fluxo.");
            carregarFila();
        } else {
            const erro = await response.text();
            alert("Atenção: " + erro);
        }
    } catch (error) {
        alert("Erro de conexão.");
    }
}