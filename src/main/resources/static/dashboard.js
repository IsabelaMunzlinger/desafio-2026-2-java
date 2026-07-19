const token = localStorage.getItem('meuTokenJWT');
if (!token) window.location.href = '/login';

// Variável global para guardar os dados
let dadosEstatisticas = {};

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('/api/dashboard/estatisticas', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            dadosEstatisticas = await response.json();
            montarDropdown(dadosEstatisticas);
        } else {
            console.error('Erro ao buscar dados');
        }
    } catch (error) {
        console.error('Erro de conexão:', error);
    }
});

function montarDropdown(dados) {
    const select = document.getElementById('selectStatus');
    select.innerHTML = '<option value="">-- Escolha um status --</option>';

    // Pega todas os nomes dos status e cria as opções do select
    Object.keys(dados).forEach(nomeStatus => {
        select.innerHTML += `<option value="${nomeStatus}">${nomeStatus}</option>`;
    });
}

// Disparado sempre que o usuário seleciona uma opção diferente
document.getElementById('selectStatus').addEventListener('change', function() {
    const statusSelecionado = this.value;
    const boxResultado = document.getElementById('boxResultado');

    if (statusSelecionado === "") {
        boxResultado.style.display = 'none'; // Esconde se voltar para a opção vazia
        return;
    }

    // Pega a quantidade usando o nome do status como chave
    const quantidade = dadosEstatisticas[statusSelecionado];

    // Trata conforme a quantidade de pedidos
    const palavra = quantidade === 1 ? 'solicitação' : 'solicitações';

    boxResultado.innerHTML = `Existem <strong>${quantidade}</strong> ${palavra} na etapa "${statusSelecionado}"`;
    boxResultado.style.display = 'block';
});