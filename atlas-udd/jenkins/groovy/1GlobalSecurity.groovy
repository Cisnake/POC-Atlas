import hudson.security.csrf.DefaultCrumbIssuer
import java.util.logging.Logger
import jenkins.*;
import jenkins.model.*;
import hudson.model.*;
import org.yaml.snakeyaml.Yaml

Jenkins jenkins = Jenkins.getInstance()

// disable remoting cli
jenkins.getDescriptor("jenkins.CLI").get().setEnabled(false)

// disabled CLI access over TCP listener (separate port)
def p = AgentProtocol.all()
p.each { x ->
    if (x.name && x.name.contains("CLI")) {
        p.remove(x)
    }
}

// disable CLI access over /cli URL
def removal = { lst ->
    lst.each { x ->
        if (x.getClass().name.contains("CLIAction")) {
            lst.remove(x)
        }
    }
}

removal(jenkins.getExtensionList(RootAction.class))
removal(jenkins.actions)

if(jenkins.getCrumbIssuer() == null) {
        jenkins.setCrumbIssuer(new DefaultCrumbIssuer(true))
        jenkins.save()
        println 'CSRF Protection configuration has changed.  Enabled CSRF Protection.'
    }
    else {
        println 'Nothing changed.  CSRF Protection already configured.'
}