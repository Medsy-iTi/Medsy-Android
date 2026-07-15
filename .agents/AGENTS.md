# AGENTS.md — Medsy Android

**Read this file fully before writing or editing code.** It is the source of truth for how the Medsy
Android project is structured. Do not introduce a new pattern, library, module dependency, result
wrapper, or shared abstraction without following the deviation protocol in §13.

**Scope:** these rules apply to all new and modified code. Existing placeholder screens and scaffold
code are grandfathered; do not mass-refactor them. When a task turns a placeholder into a real
feature, bring only that feature's touched files into compliance.

---

## 0. Golden rules

1. **Stay inside the assigned feature.** Do not edit another feature's files without explicit
   approval. Shared changes must be minimal, necessary, and called out.
2. **Respect module boundaries.** Presentation depends on domain and the design system, never data.
   Data depends on domain, never presentation. Domain stays pure Kotlin.
3. **Ask before any big change.** Do not start a broad refactor, cross-feature change, shared API
   redesign, module/package move, dependency addition/upgrade, or multi-feature migration without
   explaining the plan and receiving explicit approval.
4. **Use the design system only.** UI colors, typography, shapes, app fonts, and reusable visual
   primitives come from `:designsystem`, especially its `ui/theme` package. Hex colors are allowed
   only in design-token files. If a task genuinely requires a color from anywhere else, stop and ask
   for permission first.
5. **Never hardcode user-facing text.** Every label, message, title, accessibility description, and
   other app-authored text must come from a string resource in the module that owns the UI. Add both
   English and Arabic values.
6. **Never put API keys or secrets directly in the app.** Store developer-local values in
   gitignored `local.properties` and expose only the required value through generated `BuildConfig`.
   Never commit, print, or log the value.
7. **Never create or run tests, compile, build, install, or launch unless explicitly asked.** Do not
   run Gradle compile/build/assemble tasks, Android Studio sync, `installDebug`, an emulator, or the
   app unless the user's current task explicitly requests that action.
8. **Do not invent `MedResult`.** The project does not have `MedResult`, a shared typed error model,
   or a safe-call utility yet. Do not create a feature-specific replacement or claim a shared
   contract exists.
9. **No sensitive medical data in source or logs.** Never log passwords, OTPs, tokens,
   prescriptions, addresses, or medicine-request bodies. OkHttp BODY logging remains debug-only and
   authentication/cookie headers remain redacted.
10. **Medsy is request-based, not an inventory catalog.** Do not design flows that assume a pharmacy
    inventory feed. Estimated medicine prices are informational; pharmacist-entered final prices
    are the only prices used in financial totals.
11. **Do not perform drive-by cleanup.** No unrelated renames, formatting sweeps, dependency
    upgrades, package moves, or placeholder rewrites while implementing a scoped feature.

---

## 1. Project overview and current state

- **Product:** Medsy Android patient application.
- **Application ID / package root:** `com.medsy.medsy` / `com.medsy.*`.
- **UI:** Kotlin and Jetpack Compose, single activity.
- **Architecture:** Clean Architecture split into Gradle modules by layer, with packages grouped by
  feature inside each layer.
- **Presentation pattern:** lean MVI using `State`, `UIIntent`, and optional one-off `UIEffect`
  types.
- **Navigation:** AndroidX Navigation 3, owned by `:app`, using serializable `NavKey` routes.
- **Dependency injection:** Hilt with KSP.
- **Networking:** Retrofit + Moshi + OkHttp in `:data`.
- **Images / animation:** Coil and Lottie in `:presentation`.
- **Theme:** light and dark schemes selected from the system setting; theme tokens live in
  `:designsystem`.
- **Localization:** English and Arabic; all new UI must be RTL-safe.

Known scaffold state:

- `ApiService` is empty and the Retrofit base URL is a placeholder.
- `MedResult`, shared error types, and shared API safe-call helpers do not exist.
- `:domain` and most of `:data` are still scaffolds.
- Several screens and routes are placeholders, including AI chat and onboarding. Their existence
  does not make them approved production scope.
- Some scaffold screens still contain hardcoded text and `TODO()` code. Do not copy those patterns
  into real features.
- Template test files exist, but no new tests are required unless a task explicitly requests them.

---

## 2. Modules and dependency direction

| Module          | Responsibility                                                                                  | May depend on                                        | Must not contain                                                       |
|-----------------|-------------------------------------------------------------------------------------------------|------------------------------------------------------|------------------------------------------------------------------------|
| `:app`          | Application, activity, Navigation 3 routes/back stacks, top-level composition, Android manifest | `:presentation`, `:data`, `:domain`, `:designsystem` | Feature business logic, DTO mapping, repository implementations        |
| `:presentation` | Feature screens, ViewModels, MVI contracts, feature-local UI components                         | `:domain`, `:designsystem`                           | Retrofit/API types, repository implementations, app navigation classes |
| `:domain`       | Plain models, repository interfaces, use cases, business rules                                  | Kotlin/JDK only                                      | Android, Compose, Retrofit, Moshi, DTOs, `Context`, resources          |
| `:data`         | Retrofit APIs, DTOs, data sources, repository implementations, mappers, data-layer DI           | `:domain`                                            | Composables, ViewModels, presentation state, navigation                |
| `:designsystem` | Medsy theme, color/typography/shape tokens, fonts, feature-agnostic reusable UI                 | Compose/Android UI libraries only                    | Feature state, use cases, repositories, API/data types                 |

Allowed dependency graph:

```text
:app ─────────► :presentation ─────► :domain
  │                    │
  │                    └───────────► :designsystem
  ├───────────► :data ─────────────► :domain
  ├───────────► :domain
  └───────────► :designsystem
```

Dependencies point inward. The required runtime call chain is:

```text
Composable → ViewModel → UseCase → Repository interface → Repository implementation → DataSource → ApiService
```

- ViewModels depend on use cases, not repositories or data sources.
- Use cases depend on domain repository interfaces.
- Repository implementations are the only layer that coordinates data sources and maps data models.
- Data sources are the only classes that call Retrofit APIs or storage frameworks.
- DTOs and Retrofit `Response` objects never leave `:data`.

---

## 3. Feature and package structure

Use the same lowercase feature name across modules. For a feature named `search`:

```text
presentation/src/main/java/com/medsy/presentation/search/
├── SearchScreen.kt
├── SearchViewModel.kt
├── SearchState.kt
├── SearchUIIntent.kt
├── SearchUIEffect.kt            # only when one-off effects exist
└── components/                 # UI used only by search

domain/src/main/java/com/medsy/domain/search/
├── model/
├── repository/                 # interfaces only
└── usecase/

data/src/main/java/com/medsy/data/search/
├── remote/                     # ApiService-facing data source and DTOs
├── mapper/
├── repository/                 # implementations only
└── di/                         # bindings/providers owned by search
```

Keep small features flat until subfolders improve clarity. Do not create empty folders or classes
just to match the tree.

Shared-code placement:

- Feature-specific code remains under that feature in its owning layer module.
- A visual component used by one feature belongs in `presentation/<feature>/components`.
- A feature-agnostic visual primitive genuinely used by multiple features belongs in
  `:designsystem`.
- Shared domain concepts belong under `domain/common`; shared data infrastructure belongs under
  `data/common` or the existing data-level `di`/`remote` package.
- Do not create a new `core` module or a new Gradle feature module without team approval.
- One feature must not import another feature's presentation package. Cross-feature behavior goes
  through domain interfaces or app-level navigation callbacks.

---

## 4. Feature ownership and change isolation

Before editing, identify the task's target feature. A normal feature task may touch:

- that feature's packages in `:presentation`, `:domain`, and `:data`;
- the minimum app navigation registration needed to expose the feature;
- the owning module's string resources;
- `:designsystem` only when a genuinely shared primitive is required.

It may not silently touch another feature.

### Approval required before large changes

The agent must explain the intended change and wait for explicit approval **before editing** when
any of these are true:

- more than one feature is affected;
- a shared public interface, repository contract, navigation contract, design-system API, or data
  model changes;
- a module, package, or large group of files will be created, moved, renamed, or deleted;
- a new dependency, plugin, SDK, service, or architectural abstraction is proposed;
- the change is a broad refactor, migration, generated rewrite, or formatting sweep;
- the likely blast radius is unclear or the change could disrupt another team member's work.

The approval request must state the goal, proposed files/modules, reason, alternatives, and expected
blast radius. Do not split a large unapproved change into smaller edits to bypass this rule.

If work appears to require another feature or shared infrastructure:

1. Stop before making that edit.
2. Name the other feature/files and explain why they are required.
3. Describe the blast radius and backward-compatibility impact.
4. Wait for approval, unless the requested task explicitly includes that shared work.

When modifying a shared API or component:

- inspect all current consumers first;
- prefer additive, backward-compatible parameters with defaults;
- do not change existing behavior for unrelated consumers;
- migrate all affected consumers only when that migration is explicitly in scope;
- never duplicate a shared component to avoid coordination.

---

## 5. MVI presentation contract

The established Medsy convention uses separate files per screen:

```kotlin
data class SearchState(
    val isLoading: Boolean = false,
)

sealed interface SearchUIIntent {
    data class QueryChanged(val query: String) : SearchUIIntent
    data object Retry : SearchUIIntent
}

sealed interface SearchUIEffect {
    data class OpenProduct(val id: String) : SearchUIEffect
}
```

ViewModel rules:

- Use `@HiltViewModel` and constructor injection.
- Keep mutable state private as `_state: MutableStateFlow<XState>`.
- Expose state with the existing
  `onStart { ... }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), XState())`
  pattern when initial loading is needed.
- Update state with `_state.update { it.copy(...) }`.
- Expose one `onIntent(intent)` entry point and use an exhaustive `when`; never leave
  `else -> TODO()` in implemented features.
- Use a `Channel<XUIEffect>(capacity = Channel.BUFFERED)` plus `receiveAsFlow()` only for one-off
  navigation or UI effects.
- Do not store `Context`, composables, navigation back stacks, Retrofit types, DTOs, or resolved
  localized strings in a ViewModel.
- Keep business rules and reusable validation in use cases, not composables.

Screen split:

```kotlin
@Composable
fun SearchRoot(
    openProduct: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchUIEffect.OpenProduct -> openProduct(effect.id)
            }
        }
    }

    SearchScreen(state = state, onIntent = viewModel::onIntent)
}
```

- `XRoot` integrates the ViewModel and navigation callbacks.
- `XScreen` renders state and emits intents; it should be previewable without Hilt.
- Child composables receive values and callbacks, never a ViewModel.
- Loading, empty, error, and success states must be explicit for real data-backed screens. Add
  offline behavior when the feature has an approved connectivity source.

---

## 6. Navigation 3

- `:app` owns `Route`, root/nested back stacks, entry providers, transitions, and bottom navigation.
- Route keys implement `NavKey` and are `@Serializable`.
- Pass only stable identifiers and small navigation arguments. Load full data through the feature's
  use case.
- `:presentation` exposes callbacks such as `openProduct(id)` and must not import
  `com.medsy.medsy.nav`.
- Register a feature route with the smallest possible edit; do not reorganize unrelated routes or
  placeholder navigation.
- Use the shared navigation duration constant instead of duplicating transition timing.

---

## 7. Networking and error handling

Current shared networking lives in `:data`:

- `NetworkModule` provides OkHttp, Retrofit, and `ApiService`.
- BODY logging is installed only when `com.medsy.data.BuildConfig.DEBUG` is true.
- Authorization and cookie headers must remain redacted.
- Base URLs and environment configuration belong in build configuration, not feature source files.
- Never add a second Retrofit/OkHttp singleton for an ordinary Medsy API feature.

### Current result-contract status

There is **no** `MedResult`, `MedError`, `EmptyResult`, or shared safe-call helper yet. Therefore:

- Do not reference those types in new code or documentation.
- Do not create a result wrapper or error hierarchy inside one feature.
- Do not expose Retrofit `Response`, DTOs, Moshi types, status-code handling, or network exceptions
  to presentation.
- When an API feature is assigned before the shared result contract exists, stop and agree on that
  feature's repository failure contract with the maintainer before implementation.
- When a shared Medsy result/error contract is introduced, it must be a dedicated cross-feature task
  in `:domain`/`:data`, document cancellation behavior and mappings, migrate affected consumers
  deliberately, and update this section.

Regardless of the future wrapper, coroutine cancellation must never be converted into a normal
failure. Rethrow `CancellationException` and preserve cancellation with `ensureActive()` around
broad exception handling.

DTO rules:

- DTO names end in `Dto` and stay in `:data`.
- Convert with explicit `toDomain()` mapper functions in `:data`.
- Domain models represent Medsy concepts, not API wire shapes.
- Repository interfaces return domain types/contracts only.

---

## 8. Design system and UI consistency

The only source of Medsy visual tokens is:

```text
designsystem/src/main/java/com/medsy/designsystem/ui/theme/
```

- Always use `MedsyTheme`, `MaterialTheme.colorScheme`, `MaterialTheme.typography`, the shared
  shapes,
  `LocalExtendedColors`, and the app fonts supplied by `:designsystem`.
- Both light and dark schemes are supported through the system theme.
- Keep dynamic Material You colors disabled unless the team explicitly approves a brand change.
- Do not place color hex values in feature composables or presentation state.
- Do not use `Color(...)`, `colorResource(...)`, Android framework colors, locally defined palettes,
  or colors from another library in feature UI. Read colors from the Medsy theme.
- Do not duplicate or replace fonts, colors, shapes, typography, or semantic color definitions in
  `:app` or `:presentation`.
- If the Medsy theme does not contain a genuinely required color or visual token, stop and ask for
  permission before adding or sourcing it. After approval, add the token centrally in
  `:designsystem`; do not place a one-off color in the feature.
- Use start/end rather than left/right and verify directional icons for RTL.
- Provide content descriptions for meaningful icons/images; decorative visuals use `null`.
- Respect accessible touch targets and never communicate status by color alone.
- Use stable keys for lazy lists.

Reusable components:

- Search the feature's `components` folder and `:designsystem` before creating a component.
- A component is promoted to `:designsystem` only when it is feature-agnostic and has at least two
  real consumers, or when design explicitly defines it as a shared primitive.
- Shared components accept neutral UI data and callbacks; they never know about feature ViewModels,
  repositories, or domain workflows.

---

## 9. Strings, Arabic, and RTL

- Store every app-authored user-facing string in the resource set of the module that renders it.
  This includes button labels, headings, body text, dialogs, snackbar/toast messages, validation
  text, accessibility descriptions, and preview text intended to represent real UI.
- Add English and Arabic translations in the same change for every new or changed string.
- Use `stringResource`, placeholders, and plurals; do not concatenate translated sentences.
- Do not use hardcoded text temporarily and promise to extract it later. Create the string resources
  as part of the original change.
- Prefix resource names by feature, for example `search_empty_message` or `auth_login_title`.
- Use Western digits where required by the product specification and Egyptian Pound formatting for
  prices.
- Server-provided medicine/leaflet text may remain raw because it is content, not an app-authored UI
  label.
- Existing hardcoded placeholder labels are grandfathered. Replace them when implementing that
  screen; do not copy them.

---

## 10. Product and safety invariants

- The patient creates a medicine request; nearby pharmacies respond with offers. Do not add
  inventory-dependent browsing behavior.
- Estimated prices are labeled approximate and are never used as final financial totals.
- Final totals use only pharmacist-entered final prices plus an explicit delivery-fee line when
  applicable.
- Prescription extraction is assistive. AI output must be reviewed and must never auto-confirm or
  auto-submit an order.
- Leaflet content is retrieved verbatim from the approved medicine database, not generated.
- Delivery addresses, prescription images, tokens, phone numbers, and pharmacy licensing data are
  sensitive. Keep them scoped to the feature and authorized recipient.
- Placeholder routes such as AI chat are not authorization to implement post-MVP behavior.

---

## 11. Dependency injection and configuration

- Keep `@HiltAndroidApp` in `MedsyApp` and `@AndroidEntryPoint` on `MainActivity`.
- ViewModels use `@HiltViewModel` with constructor injection.
- Bind repository interfaces to implementations with `@Binds`; use `@Provides` for framework clients
  or builders.
- Put feature bindings in that feature's `data/<feature>/di` package. Keep application-wide
  networking in the existing data DI package.
- Scope clients and true shared services as `@Singleton`; do not singleton-scope feature state.
- Do not pass Activity or View instances into singletons.
- Add dependencies through `gradle/libs.versions.toml`; do not hardcode library versions in module
  build files.
- Do not upgrade AGP, Kotlin, Compose, Hilt, Navigation, or SDK levels as part of a feature task.

API keys and secrets:

- Put developer-local API keys and secrets in the repository's gitignored `local.properties`; never
  put them in Kotlin, XML resources, manifests, Gradle files, JSON assets, or version-controlled
  configuration.
- Read the property in Gradle and expose only the minimum required field through generated
  `BuildConfig` for the module that needs it.
- Never add a real key as a fallback/default value. Missing configuration must fail clearly or
  disable the affected development-only integration.
- Never log a key or include it in an exception, URL, analytics event, screenshot, test fixture, or
  build output shared with others.
- Client apps cannot truly protect embedded secrets. Privileged/server secrets must remain on the
  backend; `local.properties` is only acceptable for values authorized to be present in that local
  client build.

---

## 12. Naming and code conventions

- Packages lowercase; classes/composables PascalCase; functions and properties camelCase; resources
  lower_snake_case.
- Screen files/types use `XScreen`, `XRoot`, `XViewModel`, `XState`, `XUIIntent`, and optional
  `XUIEffect`.
- Domain repositories use `XRepository`; implementations use `XRepositoryImpl`.
- Data transfer models use `XDto`; mapper functions use `toDomain()` / `toDto()` where required.
- Use cases use `VerbNounUseCase` and `operator fun invoke`.
- Keep files focused; one primary public composable or type per file where practical.
- Prefer the simplest design that maintains the module boundaries. Do not add generic base
  ViewModels, reducers, MVI frameworks, or abstraction layers without a demonstrated shared need.

---

## 13. Deviation protocol

If a requirement cannot be implemented within these rules:

1. Stop before deviating.
2. Cite the rule that blocks the work.
3. Explain the concrete reason and affected modules/features.
4. Propose the smallest deviation and one compliant alternative.
5. Wait for explicit approval.
6. If the approval establishes a reusable convention, update this file in the same change.

Security, privacy, medical-safety, and feature-isolation rules are hard stops. Do not silently work
around them.

---

## 14. Testing and verification

- **Do not create, generate, edit, or run unit tests, instrumentation tests, UI tests, snapshots,
  benchmarks, or test fixtures unless the user explicitly asks for tests in the current task.** Do
  not add testing dependencies speculatively.
- Still write production code with constructor-injected dependencies, pure domain models, explicit
  mappers, and framework-free business logic so tests can be added later.
- Do not edit generated build output.
- **Do not compile, build, assemble, install, launch, sync, or run the app unless the user
  explicitly
  asks for that exact kind of verification in the current task.** This includes Gradle
  compile/build/assemble/check tasks, `installDebug`, Android Studio sync, emulator/device launch,
  and background or fire-and-forget runs.
- Verify unrequested work through static inspection only: check imports, types, module dependencies,
  resource names, exhaustive branches, and obvious syntax issues.
- At handoff, state that tests and build/run verification were not executed because this project
  requires explicit permission.
- If the user explicitly requests verification, run only the requested scope and report the exact
  command and result. Never claim a test, compilation, build, or app run passed without executing
  it.

---

## 15. Definition of Done

- [ ] Work stayed inside the target feature plus explicitly required shared integration points.
- [ ] Module dependencies and the full UI-to-data call chain are respected.
- [ ] No DTO, Retrofit, Android, or Compose types leaked into the wrong layer.
- [ ] No new result wrapper was introduced while `MedResult` remains undefined.
- [ ] MVI state/intents/effects are lean and exhaustive; no new placeholder `TODO()` remains.
- [ ] Feature UI uses `:designsystem`, supports light/dark, and contains no hardcoded colors.
- [ ] App fonts and all UI tokens come from the Medsy theme/design system; any exception received
  explicit approval.
- [ ] All app-authored user-facing text comes from string resources, exists in English and Arabic,
  and is RTL-safe.
- [ ] No credentials or sensitive data are committed or logged.
- [ ] API keys are sourced from gitignored `local.properties` through the minimum required generated
  configuration; privileged secrets remain on the backend.
- [ ] Medsy pricing, AI safety, and privacy invariants are preserved.
- [ ] No unrelated feature, dependency, or formatting changes were included.
- [ ] No tests were created or run and no compile/build/install/sync/app run was started unless
  explicitly requested.
- [ ] Any large or cross-feature change received explicit approval before editing began.
