package world.reveal.sitemap
package entities

object PublicationStatus extends Enumeration{
  type PublicationStatus = Value
  val Private, ForModeration, Published, Deleted, Denied = Value
}
