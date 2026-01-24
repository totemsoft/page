import * as cdk from 'aws-cdk-lib/core';
import { Construct } from 'constructs';
import { AwsLogDriver, Cluster, ContainerDefinition, ContainerImage, FargateTaskDefinition } from 'aws-cdk-lib/aws-ecs';
import { ApplicationLoadBalancedFargateService } from 'aws-cdk-lib/aws-ecs-patterns';
import { ApplicationProtocol, Protocol } from 'aws-cdk-lib/aws-elasticloadbalancingv2';
import { HostedZone } from 'aws-cdk-lib/aws-route53';
import { EnvironmentUtils } from './include/environment-utils';
import { PolicyStatement } from 'aws-cdk-lib/aws-iam';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as efs from 'aws-cdk-lib/aws-efs';
import * as iam from 'aws-cdk-lib/aws-iam';
import { Certificate, CertificateValidation } from 'aws-cdk-lib/aws-certificatemanager';

export interface AwsCdkStackProps extends cdk.StackProps {

  /**
   * The name of the VPC
   * @type {string}
   * @memberof AwsCdkStackProps
   */
  readonly vpcName?: string;

  /**
   * The zone domain e.g. example.com
   * @type {string}
   * @memberof AwsCdkStackProps
   */
  readonly domainName: string;

}

export class AwsCdkStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props: AwsCdkStackProps) {
    super(scope, id, props);

/*
    // Java
    const containerImage= 'totemsoft/page-builder'; // :latest
    const taskCpu = 1024;
    const taskMemoryLimitMiB = 2048;
    const javaOpts = '-Xms1024m -Xmx1536m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=256m -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true';
//*/
//*
    // GraalVM
    const containerImage= 'totemsoft/page-builder-graalvm'; // :latest
    const taskCpu = 256;
    const taskMemoryLimitMiB = 512;
    const javaOpts = null;
//*/
    // EFS
    const efsVolumeName = 'efsVolume';
    const efsMountPath = '/mnt/efs/db';
    const dbName = 'pagedb_004';
 
    const domainName = props.domainName;

    const vpc = ec2.Vpc.fromLookup(this, id, {vpcName: props.vpcName});
    const vpcSubnets: ec2.SubnetSelection = {
      subnetType: ec2.SubnetType.PUBLIC
    };

    const cluster = new Cluster(this, `${id}Cluster`, {
      vpc
    });

    const sg = new ec2.SecurityGroup(this, `${id}System`, {
      vpc,
      allowAllOutbound: false,
      description: `${id} ALB`,
      securityGroupName: `${id}ALB`
    });
    sg.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(443), 'Inbound HTTPS');
    sg.addIngressRule(ec2.Peer.ipv4(vpc.vpcCidrBlock), ec2.Port.tcp(2049), 'Inbound NFS');
    sg.addEgressRule(ec2.Peer.anyIpv4(), ec2.Port.allTcp(), 'Outbound');

    //const account = process.env.CDK_DEFAULT_ACCOUNT;
    //const region = process.env.CDK_DEFAULT_REGION;
    const fileSystemPolicy = new iam.PolicyDocument({
        statements: [
            new iam.PolicyStatement({
                effect: iam.Effect.ALLOW,
                actions: [
                   'elasticfilesystem:ClientMount',
                   'elasticfilesystem:ClientRootAccess',
                   'elasticfilesystem:ClientWrite'
                ],
                principals: [
                    new iam.StarPrincipal()
                    //new iam.ArnPrincipal(`arn:aws:elasticfilesystem:${region}:${account}:file-system/fs-???`)
                ],
                resources: ['*'],
                conditions: {
                    Bool: {
                        'elasticfilesystem:AccessedViaMountTarget': 'true'
                    }
                },
            })
        ]
    });
    const fileSystem = new efs.FileSystem(this, `${id}EfsFileSystem`, {
        vpc: vpc,
        vpcSubnets: vpcSubnets,
        securityGroup: sg,
        fileSystemPolicy: fileSystemPolicy,
        performanceMode: efs.PerformanceMode.GENERAL_PURPOSE,
        lifecyclePolicy: efs.LifecyclePolicy.AFTER_7_DAYS,
        outOfInfrequentAccessPolicy: efs.OutOfInfrequentAccessPolicy.AFTER_1_ACCESS,
        //removalPolicy: cdk.RemovalPolicy.DESTROY,
        encrypted: true, // Transit encryption must be enabled if IAM authorization is used
    });

    const taskDef = new FargateTaskDefinition(this, `${id}TaskDefinition`, {
      cpu: taskCpu,
      memoryLimitMiB: taskMemoryLimitMiB
    });
    const taskPolicy = new PolicyStatement({
        actions: [
            'elasticfilesystem:ClientWrite'
        ],
        resources: ['*']
    });
    taskDef.addToTaskRolePolicy(taskPolicy);
    taskDef.addVolume({
        name: efsVolumeName,
        efsVolumeConfiguration: {
            fileSystemId: fileSystem.fileSystemId,
            transitEncryption: 'ENABLED', // ecs.EfsTransitEncryption.ENABLED,
        },
    });

    // create a task definition with CloudWatch Logs
    const logDriver = new AwsLogDriver({
      streamPrefix: `ecs-${id}`
    });
    const containerDef = new ContainerDefinition(this, `${id}ContainerDefinition`, {
      image: ContainerImage.fromRegistry(containerImage),
      taskDefinition: taskDef,
      environment: {
        PROFILE: id,
        STAGE: 'dev',
        EFS_MOUNT_PATH: efsMountPath,
        DB_NAME: dbName,
      },
      logging: logDriver,
      portMappings: [
        { containerPort: 8080, name: 'page-builder-http' }
      ]
    });
    EnvironmentUtils.addEnvironments(containerDef, javaOpts);

    containerDef.addMountPoints({
        sourceVolume: efsVolumeName,
        containerPath: efsMountPath,
        readOnly: false,
    });

    const domainZone = HostedZone.fromLookup(this, 'Zone', {
      domainName
    });

    const certificate = new Certificate(this, `${id}Certificate`, {
      domainName: `${id}.${domainName}`,
      validation: CertificateValidation.fromDns(domainZone)
    });

    // Create a load-balanced Fargate service and make it public
    const albFargateService = new ApplicationLoadBalancedFargateService(this, `${id}FargateService`, {
      cluster,
      taskDefinition: taskDef,
      taskSubnets: vpcSubnets,
      desiredCount: 1,
      publicLoadBalancer: true,
      assignPublicIp: true,
      circuitBreaker: {
          enable: true,
          rollback: true
      },
      domainName: `${id}.${domainName}`,
      domainZone,
      certificate,
      protocol: ApplicationProtocol.HTTPS,
      targetProtocol: ApplicationProtocol.HTTP,
      securityGroups: [sg],
      minHealthyPercent: 50,
      maxHealthyPercent: 200,
      idleTimeout: cdk.Duration.seconds(60),
      healthCheckGracePeriod: cdk.Duration.seconds(60)
    });

    const targetGroup = albFargateService.targetGroup;
    targetGroup.setAttribute('deregistration_delay.timeout_seconds', '30');
    targetGroup.configureHealthCheck({
      healthyHttpCodes: '200,302',
      path: '/',
      protocol: Protocol.HTTP,
      port: '8080',
      healthyThresholdCount: 3,
      interval: cdk.Duration.seconds(30)
    });
  }
}
