/** Plugin for module without UI like core data or domain */
open class NonUiPlugin: ModulePlugin() {
    override val isUi = false
}