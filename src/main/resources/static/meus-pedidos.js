const token = localStorage.getItem('meuTokenJWT');
if (!token) window.location.href = '/login';

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('/api/solicitacoes/meus-pedidos', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const pedidos = await response.json();
            const tbody = document.getElementById('tabelaPedidosBody');

            pedidos.forEach(p => {
                const isEmitido = p.status.nome.toLowerCase() === 'emitido';
                const classeStatus = isEmitido ? 'status-emitido' : 'status-andamento';

                const acaoDownload = isEmitido
                    ? `<button class="btn-baixar" onclick="baixarPdf(${p.id})"> Baixar PDF</button>`
                    : `<span class="status-aguardando">Aguardando...</span>`;

                const acaoHistorico = `<button class="btn-historico" onclick="window.location.href='/historico-pedido?id=${p.id}'">Ver Histórico</button>`;

                let dataFormatada = "Indisponível";
                if (p.dataSolicitacao) {
                    const [dataParte, horaParte] = p.dataSolicitacao.split('T');
                    const [ano, mes, dia] = dataParte.split('-');
                    const horaMinuto = horaParte.substring(0, 5); // Pega apenas o HH:MM
                    dataFormatada = `${dia}/${mes}/${ano} às ${horaMinuto}`;
                }

                tbody.innerHTML += `
                    <tr>
                        <td>#${p.id}</td>
                        <td><strong>${p.tipo.nome}</strong></td>
                        <td>${dataFormatada}</td>
                        <td class="${classeStatus}">${p.status.nome}</td>
                        <td>${acaoDownload} ${acaoHistorico}</td>
                    </tr>
                `;
            });
        }
    } catch (error) {
        console.error("Erro ao buscar pedidos", error);
    }
});

async function baixarPdf(id) {
    const response = await fetch(`/api/solicitacoes/${id}/download`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });

    if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `documento_${id}.pdf`;
        document.body.appendChild(a);
        a.click();
        a.remove();
    } else {
        alert("Erro ao tentar baixar o documento.");
    }
}