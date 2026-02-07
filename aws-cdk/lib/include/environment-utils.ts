import { SecretValue, Stack } from 'aws-cdk-lib';
import * as ecs from 'aws-cdk-lib/aws-ecs';
import * as sm from 'aws-cdk-lib/aws-secretsmanager';

export class EnvironmentUtils {
  static addEnvironments(stack: Stack, containerDef: ecs.ContainerDefinition,
      javaOpts: string | null | undefined): void {
    const stage = 'dev';
    const smPartialArn = `arn:aws:secretsmanager:${stack.region}:${stack.account}:secret`;

    containerDef.addEnvironment('PROFILE', stack.stackName);
    containerDef.addEnvironment('STAGE', stage);
    containerDef.addEnvironment('TZ', 'Australia/Brisbane');
    if (!!javaOpts) {
      containerDef.addEnvironment('JAVA_OPTS', javaOpts);
    }

    // Google
    const googleSecretName = `${stage}/${stack.stackName}/google/credentials`;
    const googleSecret = sm.Secret.fromSecretAttributes(stack, googleSecretName, {
      secretPartialArn: `${smPartialArn}:${googleSecretName}`
    });
    //containerDef.addEnvironment('GOOGLE_CLIENT_ID', EnvironmentUtils._getValueFromSecret(googleSecret, 'client_id'));
    containerDef.addSecret('GOOGLE_CLIENT_ID', ecs.Secret.fromSecretsManager(googleSecret, 'client_id'));
    containerDef.addSecret('GOOGLE_CLIENT_SECRET', ecs.Secret.fromSecretsManager(googleSecret, 'client_secret'));

    // https://exchangeratesapi.io/
    const exchangeRatesSecretName = `${stage}/${stack.stackName}/exchangeRates/credentials`;
    const exchangeRatesSecret = sm.Secret.fromSecretAttributes(stack, exchangeRatesSecretName, {
      secretPartialArn: `${smPartialArn}:${exchangeRatesSecretName}`
    });
    containerDef.addSecret('EXCHANGERATESAPI_ACCESS_KEY', ecs.Secret.fromSecretsManager(exchangeRatesSecret, 'access_key'));
  }
  private static _getValueFromSecret(secret: sm.ISecret, key: string): string {
    return secret.secretValueFromJson(key).unsafeUnwrap();
  }
}
