variable "aws_region" {
  description = "Región de AWS donde se despliega"
  type        = string
  default     = "eu-north-1"
}

variable "project_name" {
  description = "Nombre del proyecto, se usa en nombres y etiquetas"
  type        = string
  default     = "polyparrot"
}

variable "instance_type" {
  description = "Tipo de instancia EC2 (x86; evita los t4g, que son ARM)"
  type        = string
  default     = "t3.large"
}

variable "repo_url" {
  description = "Repositorio público con el docker-compose de Polyparrot"
  type        = string
  default     = "https://github.com/jose200112/polyparrot-app.git"
}
