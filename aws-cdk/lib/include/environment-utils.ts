import { ContainerDefinition } from 'aws-cdk-lib/aws-ecs';

export class EnvironmentUtils {
  static addEnvironments(containerDef: ContainerDefinition,
      javaOpts: string | null | undefined
    ): void {
    containerDef.addEnvironment('TZ', 'Australia/Brisbane');
    if (!!javaOpts) {
      containerDef.addEnvironment('JAVA_OPTS', javaOpts);
    }
  }
}
