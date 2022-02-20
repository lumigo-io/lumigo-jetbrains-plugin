package actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class OpenFunctionInLumigo extends AnAction {
    String name = "";

    public OpenFunctionInLumigo(String functionName){
        this.name = functionName;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final String baseUrl = "https://platform.lumigo.io/functions?timespan=LAST_HOUR&searchTerm=";
        String url = baseUrl.concat(this.name);
        BrowserUtil.browse(url);
    }
}
