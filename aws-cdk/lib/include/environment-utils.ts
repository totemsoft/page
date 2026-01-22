import { ContainerDefinition } from 'aws-cdk-lib/aws-ecs';

export class EnvironmentUtils {
  static addEnvironments(containerDef: ContainerDefinition, efsMountPath: string, javaOpts: string | null | undefined): void {
    containerDef.addEnvironment('TZ', 'Australia/Brisbane');
    containerDef.addEnvironment('EFS_MOUNT_PATH', efsMountPath);
    if (!!javaOpts) {
      containerDef.addEnvironment('JAVA_OPTS', javaOpts);
    }
  }
}
