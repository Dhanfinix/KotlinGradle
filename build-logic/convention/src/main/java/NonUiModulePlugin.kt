/** Plugin for module without UI like core data or domain */
open class NonUiPlugin: ModulePlugin() {
    override val isApp = false
    override val isUi = false
}