package com.github.gililumigo.lumigointellijplugin.listeners

import actions.OpenFunctionInLumigo
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.*
import com.intellij.refactoring.suggested.startOffset
import icons.LumigoPluginIcons
import javax.swing.Icon

internal class MyProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.messageBus.connect(project)
            .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
                override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                    super.fileOpened(source, file)

                    if (file.name != "serverless.yml") {
                        return
                    }

                    val psiFile: PsiFile? = PsiManager.getInstance(project).findFile(file);

                    // Get 'functions' psi element from serverless.yml file
                    val functionsSection: PsiElement = PsiTreeUtil.collectElements(psiFile) { element ->
                        element.firstChild?.text.toString() == "functions"
                    }[0]

                    // Get the actual functions psi elements from whiten the function element.
                    val functions = PsiTreeUtil.collectElements(functionsSection) { element ->
                        element.navigationElement.toString() == "YAML key value" &&
                                element.firstChild.text.toString() == "name"
                    }

                    // Get document instance for calculating line numbers.
                    val fileViewProvider = psiFile?.viewProvider
                    val document = fileViewProvider?.document

                    // Get markup instance for appending the gutter icons.
                    val editor: Editor = FileEditorManager.getInstance(project).selectedTextEditor!!
                    val markup: MarkupModel = editor.markupModel

                    // Loop through all functions add append lumigo gutter action button.
                    for (function in functions) {
                        val textOffset: Int = function.firstChild.startOffset;
                        val name = { ->function.lastChild.text.substring(maxOf(function.lastChild.text.indexOf('}') + 1,0))};
                        appendGutterIcon(markup, Action(name, document!!.getLineNumber(textOffset)));
                    }
                }
            })
    }
}

fun appendGutterIcon(markup: MarkupModel, action: Action) {
    val iconRenderer: GutterIconRenderer = object : GutterIconRenderer() {
        override fun getIcon(): Icon {
            return LumigoPluginIcons.LumigoLogo
        }

        override fun equals(other: Any?): Boolean {
            return false
        }

        override fun hashCode(): Int {
            return 0
        }

        override fun getTooltipText(): String {
            return "Open in Lumigo"
        }

        override fun getClickAction(): AnAction {
            return OpenFunctionInLumigo(action.name())
        }

        override fun isNavigateAction(): Boolean {
            return true
        }
    }

    val rangeHighlighter = markup.addLineHighlighter(action.line, 1, null)
    rangeHighlighter.gutterIconRenderer = iconRenderer
}

data class Action(var name: () -> String, var line: Int)
