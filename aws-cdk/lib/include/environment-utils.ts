import { SecretValue, Stack } from 'aws-cdk-lib';
import * as ecs from 'aws-cdk-lib/aws-ecs';
import * as sm from 'aws-cdk-lib/aws-secretsmanager';

export class EnvironmentUtils {
  static addEnvironments(stack: Stack, containerDef: ecs.ContainerDefinition,
      googleSecretName: string,
      javaOpts: string | null | undefined): void {
    const smPartialArn = `arn:aws:secretsmanager:${stack.region}:${stack.account}:secret`;

    containerDef.addEnvironment('TZ', 'Australia/Brisbane');
    if (!!javaOpts) {
      containerDef.addEnvironment('JAVA_OPTS', javaOpts);
    }

    // Google
    const googleSecret = sm.Secret.fromSecretAttributes(stack, googleSecretName, {
      secretPartialArn: `${smPartialArn}:${googleSecretName}`
    });
    //containerDef.addEnvironment('GOOGLE_CLIENT_ID', EnvironmentUtils._getValueFromSecret(googleSecret, 'client_id'));
    containerDef.addSecret('GOOGLE_CLIENT_ID', ecs.Secret.fromSecretsManager(googleSecret, 'client_id'));
    containerDef.addSecret('GOOGLE_CLIENT_SECRET', ecs.Secret.fromSecretsManager(googleSecret, 'client_secret'));

  }
  private static _getValueFromSecret(secret: sm.ISecret, key: string): string {
    return secret.secretValueFromJson(key).unsafeUnwrap();
  }
}
