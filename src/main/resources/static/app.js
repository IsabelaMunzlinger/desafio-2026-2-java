document.getElementById('formLogin').addEventListener('submit', async function(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const mensagemErro = document.getElementById('mensagemErro');

    try {
        const resposta = await fetch('/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ login: email, senha: senha })
        });

        if (resposta.ok) {
            const token = await resposta.text();

            localStorage.setItem('meuTokenJWT', token);

            mensagemErro.style.display = 'none';
            window.location.href = '/menu';
        } else {
            mensagemErro.style.display = 'block';
        }
    } catch (erro) {
        console.error("Erro na requisição:", erro);
        alert("Erro ao conectar com o servidor.");
    }
});