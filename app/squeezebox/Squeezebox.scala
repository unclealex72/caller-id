package squeezebox

trait Squeezebox[MAT] {

  def display(text: String): MAT
}
