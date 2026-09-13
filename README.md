# Solo Gym

Aplicativo Android para organizar e acompanhar treinos de musculação de forma simples, rápida e totalmente offline.

O Solo Gym permite cadastrar exercícios personalizados, montar treinos para os dias da semana, acompanhar a execução do treino diário e consultar o histórico e as estatísticas de frequência. Todos os dados permanecem armazenados localmente no aparelho.

## Funcionalidades

- Cadastro, edição e exclusão de exercícios.
- Configuração de repetições e carga individual para cada série.
- Configuração do tempo de descanso entre séries.
- Definição de exercícios substitutos.
- Seleção temporária de substitutos durante o treino, exibindo as séries, repetições, cargas e descanso da alternativa escolhida.
- Criação de um treino para cada dia da semana.
- Exibição automática do treino do dia.
- Cronômetro da sessão e marcação dos exercícios concluídos.
- Histórico local dos treinos realizados.
- Estatísticas de frequência e duração dos treinos.
- Perfil local de Player com level, experiência, streak e falhas de treino.
- Perfil pessoal com avatar predefinido, idade calculada e histórico de peso e altura, incluindo gráficos das seis medições mais recentes.
- Gamificação: treinos programados concedem 30 XP e streak; treinos voluntários em dias livres concedem 20 XP; descansos concluídos concedem 15 XP; faltas em dias programados aplicam penalidade.
- Reset do progresso do Player e do histórico, sem apagar exercícios ou treinos programados.
- Integração opcional com o aplicativo Spotify para reproduzir uma playlist e controlar a música durante o treino.
- Funcionamento completo sem conexão com a internet.

## Telas principais

O aplicativo está dividido em cinco áreas:

1. **Hoje** — exibe e permite executar o treino do dia.
2. **Treinos** — permite montar e organizar os treinos semanais.
3. **Exercícios** — gerencia os exercícios disponíveis.
4. **Estatísticas** — apresenta o progresso do Player e informações calculadas a partir do histórico.
5. **Informações** — cadastra e edita nome, data de nascimento, peso, altura e avatar predefinido do perfil local.

## Gamificação

O aplicativo mantém um único Player local. Ele começa no level 1, com `0 / 100 XP`. Cada treino programado concluído concede 30 XP e aumenta o streak em um. Em dias sem treino programado, o usuário pode escolher e realizar um dos treinos cadastrados voluntariamente, recebendo 20 XP sem alterar o streak. Se nenhum treino for realizado no dia livre, o descanso concede 15 XP após o término do dia, também sem alterar o streak. Ao atingir a experiência máxima, o level aumenta, a meta seguinte cresce 20% e qualquer XP excedente permanece disponível no novo level.

Cada treino programado pode ser concluído apenas uma vez por dia. A aba Hoje consulta o histórico da data e passa a exibir a missão como concluída, enquanto o histórico e a recompensa do Player são salvos juntos para evitar XP duplicada.

O cronômetro calcula a duração pelo tempo real transcorrido desde o início da sessão. O horário inicial e o treino em andamento são persistidos no Room, permitindo retomar automaticamente a contagem após bloqueio da tela, encerramento do processo, reinicialização do aparelho ou falta de bateria.

Quando passa um dia que possuía treino programado sem que ele tenha sido concluído, uma falha é registrada, o streak volta a zero e 10 XP são descontados. Se o Player estiver acima do level 1 e não possuir XP suficiente para a penalidade, ele perde um level, sua experiência volta a zero e a experiência máxima é reduzida em 20%. O level nunca fica abaixo de 1. A verificação é persistente e cada dia perdido é contabilizado apenas uma vez.

## Tecnologias

- Kotlin
- Jetpack Compose
- Material Design 3
- MVVM
- Room (SQLite)
- Hilt
- Kotlin Coroutines e StateFlow
- Navigation Compose
- Gradle Kotlin DSL

O fluxo principal da arquitetura é:

```text
UI (Compose) → ViewModel → Repository → DAO → Room
```

Cada série armazena sua própria quantidade de repetições e carga em quilogramas. Séries criadas sem carga e registros migrados de versões anteriores utilizam `0 kg` como valor padrão.

## Requisitos

- JDK 11 ou superior compatível com o Android Gradle Plugin utilizado.
- Android SDK instalado.
- Android SDK Platform 36.
- Um aparelho Android com Android 8.0 (API 26) ou superior, ou um emulador.

O Android Studio pode ser usado para instalar e gerenciar o SDK, mas não é obrigatório para editar ou compilar o projeto.

## Executando o projeto

Clone o repositório e abra um terminal na pasta raiz do projeto.

### Configuração opcional do Spotify

Crie um aplicativo no [Spotify Developer Dashboard](https://developer.spotify.com/dashboard), selecione Android e cadastre:

- Package name: `com.example.sologym`
- Redirect URI: `sologym://spotify-callback`
- Fingerprint SHA-1 da chave usada para assinar o APK

Depois, adicione o Client ID ao arquivo local e não versionado `local.properties`:

```properties
SPOTIFY_CLIENT_ID=seu_client_id
```

Também é possível usar a variável de ambiente `SPOTIFY_CLIENT_ID` ou o argumento Gradle `-PSPOTIFY_CLIENT_ID=...`. Sem essa configuração, o restante do aplicativo compila e funciona normalmente, mas a conexão com o Spotify permanece desabilitada.

O projeto inclui o Spotify App Remote SDK `0.8.0` em `app/libs` e utiliza o Spotify Auth `5.0.0` para abrir explicitamente a autorização do escopo `app-remote-control` antes da conexão. O SHA-256 do AAR local é `b5a6dd880eaf01f63a871cba9ef7af77c341f8a94ffc8fdf2e9021f9a9d4c198`.

No Windows, compile o APK de desenvolvimento com:

```powershell
.\gradlew.bat assembleDebug
```

O arquivo será gerado em:

```text
app/build/outputs/apk/debug/app-debug.apk
```

No Linux ou macOS, utilize:

```bash
./gradlew assembleDebug
```

## Instalando em um aparelho

Ative as **Opções do desenvolvedor** e a **Depuração USB** no aparelho. Depois de conectá-lo, confirme que ele foi reconhecido:

```powershell
adb devices
```

Compile e instale o aplicativo:

```powershell
.\gradlew.bat installDebug
```

Para abrir o aplicativo pelo terminal:

```powershell
adb shell am start -n com.example.sologym/.MainActivity
```

## Testes

Execute os testes unitários locais com:

```powershell
.\gradlew.bat testDebugUnitTest
```

Com um aparelho ou emulador conectado, execute os testes instrumentados com:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

## Capturas de tela

As capturas de tela do aplicativo serão adicionadas conforme a interface for consolidada.

## Documentação

A especificação técnica completa, incluindo requisitos funcionais, regras de negócio, arquitetura e roadmap, está disponível em [`app/GEMINI.md`](app/GEMINI.md).

## Escopo

O Solo Gym prioriza uma experiência local e objetiva. A integração com o Spotify é opcional e não interfere nas funcionalidades offline; a versão atual não inclui contas próprias, sincronização em nuvem, recursos sociais, controle de dieta ou geração automática de treinos.

## Status

Projeto em desenvolvimento.
