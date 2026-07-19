const token = localStorage.getItem('meuTokenJWT');
if (!token) window.location.href = '/login';

const urlParams = new URLSearchParams(window.location.search);
const solicitacaoId = urlParams.get('id');

document.addEventListener('DOMContentLoaded', () => {
    if (solicitacaoId) {
        document.getElementById('pedidoId').innerText = solicitacaoId;
        carregarHistorico();
    } else {
        alert("ID do pedido não informado.");
    }
});

async function carregarHistorico() {
    try {
        const response = await fetch(`/api/solicitacoes/${solicitacaoId}/historico`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const historico = await response.json();
            const container = document.getElementById('timelineContainer');
            container.innerHTML = '';

            if(historico.length === 0) {
                container.innerHTML = '<p>Nenhuma movimentação registrada.</p>';
                return;
            }

            historico.forEach(mov => {
                let dataFormatada = "Indisponível";
                if (mov.data && mov.data !== "Data Indisponível") {
                    const partes = mov.data.split('T');
                    if (partes.length === 2) {
                        const [ano, mes, dia] = partes[0].split('-');
                        const horaMinuto = partes[1].substring(0, 5);
                        dataFormatada = `${dia}/${mes}/${ano} às ${horaMinuto}`;
                    } else {
                        dataFormatada = mov.data;
                    }
                }

                container.innerHTML += `
                    <div class="timeline-item">
                        <div class="timeline-data">${dataFormatada}</div>
                        <div class="timeline-status">${mov.status}</div>
                        <div class="timeline-resp">${mov.responsavel} (${mov.perfil})</div>
                        <div class="timeline-obs">"${mov.observacao}"</div>
                    </div>
                `;
            });
        } else {
            document.getElementById('timelineContainer').innerHTML = '<p style="color:red">Erro ao carregar o histórico.</p>';
        }
    } catch (error) {
        console.error("Erro na requisição:", error);
    }
}