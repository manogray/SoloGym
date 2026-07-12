# Solo Gym

Aplicativo Android para organizar e acompanhar treinos de musculação de forma simples, rápida e totalmente offline.

O Solo Gym permite cadastrar exercícios personalizados, montar treinos para os dias da semana, acompanhar a execução do treino diário e consultar o histórico e as estatísticas de frequência. Todos os dados permanecem armazenados localmente no aparelho.

## Funcionalidades

- Cadastro, edição e exclusão de exercícios.
- Configuração de repetições e carga individual para cada série.
- Configuração do tempo de descanso entre séries.
- Definição de exercícios substitutos.
- Criação de um treino para cada dia da semana.
- Exibição automática do treino do dia.
- Cronômetro da sessão e marcação dos exercícios concluídos.
- Histórico local dos treinos realizados.
- Estatísticas de frequência e duração dos treinos.
- Perfil local de Player com level, experiência, streak e falhas de treino.
- Gamificação: treinos concluídos concedem XP e faltas em dias programados aplicam penalidade.
- Funcionamento completo sem conexão com a internet.

## Telas principais

O aplicativo está dividido em quatro áreas:

1. **Hoje** — exibe e permite executar o treino do dia.
2. **Treinos** — permite montar e organizar os treinos semanais.
3. **Exercícios** — gerencia os exercícios disponíveis.
4. **Estatísticas** — apresenta o progresso do Player e informações calculadas a partir do histórico.

## Gamificação

O aplicativo mantém um único Player local. Ele começa no level 1, com `0 / 100 XP`. Cada treino concluído concede 20 XP e aumenta o streak em um. Ao atingir a experiência máxima, o level aumenta, a experiência atual volta a zero e a próxima meta cresce 20%.

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

O Solo Gym prioriza uma experiência local e objetiva. A versão atual não inclui contas de usuário, serviços remotos, sincronização em nuvem, recursos sociais, controle de dieta ou geração automática de treinos.

## Status

Projeto em desenvolvimento.
