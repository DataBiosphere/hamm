//package org.broadinstitute.workbench.hamm
//package costUpdater
//
//import cats.effect._
//import cats.implicits._
//import io.chrisdavenport.log4cats.Logger
//import io.grpc._
////import org.lyranthe.fs2_grpc.java_runtime.implicits._
//import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
//import io.grpc.protobuf.services.ProtoReflectionService
//import fs2._
//import fs2.concurrent.Queue
//import org.broadinstitute.dsde.workbench.google2.{Event, GoogleSubscriber}
//import org.broadinstitute.workbench.hamm.model._
//import org.broadinstitute.workbench.hamm.model.CromwellMetadataJsonCodec.metadataResponseDecoder
//import org.broadinstitute.workbench.hamm.protos.costUpdater._
//
//object Main extends IOApp {
//  override def run(args: List[String]): IO[ExitCode] =  {
//    implicit val logger = Slf4jLogger.unsafeCreate[IO]
//
//    val updateCost: Sink[IO, Event[MetadataResponse]] = Sink{
//      event =>
//        //TODO: calculate cost and persist to database before acknowledging
//        IO.delay(event.consumer.ack())
//    }
//
//    val app: Stream[IO, Unit] = for {
//      appConfig <- Stream.fromEither[IO](Config.appConfig)
//
//      _ <- Stream.eval(logger.info("Starting Hamm Cost Updater Grpc server"))
//      queue <- Stream.eval(Queue.bounded[IO, Event[MetadataResponse]](10000)) //TODO: think about size of the queue a bit more
//      subscriber <- Stream.resource(GoogleSubscriber.resource(appConfig.google.subscriber, queue))
//      subscribeStream = Stream.eval_(subscriber.start)
//      processMetadataStream = subscriber.messages to updateCost
//
//      service: ServerServiceDefinition = CostUpdaterFs2Grpc.bindService(new CostUpdaterGrpcImp[IO])
//      grpcStream = ServerBuilder.forPort(9999)
//        .addService(service)
//        .addService(ProtoReflectionService.newInstance())
//        .stream[IO]
//        .evalMap(server => IO(server.start()))
//      _ <- Stream(subscribeStream, grpcStream, processMetadataStream).parJoin(50) //TODO: think more about max open
//    } yield ()
//
//    app.handleErrorWith(error => Stream.eval(logger.error(error)("Failed to start server")))
//      .evalMap(_ => IO.never)
//      .compile
//      .drain
//      .as(ExitCode.Success)
//  }
//}
//
//class CostUpdaterGrpcImp[F[_]: Sync: Logger] extends CostUpdaterFs2Grpc[F] {
//  override def status(request: CostUpdaterStatusRequest, clientHeaders: Metadata): F[CostUpdaterStatusResponse] = Sync[F].point(CostUpdaterStatusResponse(
//    BuildInfo.scalaVersion,
//    BuildInfo.sbtVersion,
//    BuildInfo.gitHeadCommit.getOrElse("No commit yet"),
//    BuildInfo.buildTime,
//    BuildInfo.toString
//  ))
//}