import { ContainerDefinition } from 'aws-cdk-lib/aws-ecs';

export class EnvironmentUtils {
  static addEnvironments(id: string, containerDef: ContainerDefinition): void {
    containerDef.addEnvironment('PROFILE', id);
    containerDef.addEnvironment('TZ', 'Australia/Brisbane');
    containerDef.addEnvironment('JAVA_OPTS', '-Xms1024m -Xmx1536m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=256m -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true');
  }
}
